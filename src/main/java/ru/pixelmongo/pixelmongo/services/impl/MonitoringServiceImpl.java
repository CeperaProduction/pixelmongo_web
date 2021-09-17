package ru.pixelmongo.pixelmongo.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.pixelmongo.pixelmongo.model.dao.primary.MonitoringServer;
import ru.pixelmongo.pixelmongo.repositories.primary.MonitoringServerRepository;
import ru.pixelmongo.pixelmongo.services.MonitoringService;

@Service
class MonitoringServiceImpl implements MonitoringService{

    @Autowired
    private MonitoringServerRepository serverData;

    @Autowired
    private Environment env;

    private boolean debug;

    private int timeout;

    private int nextPingDelay;

    private int serverCacheUpdatePeriod;

    private List<MonitoringServer> serverCache = Collections.emptyList();
    private Map<String, MonitoringResult> monitoringCache = new ConcurrentHashMap<>();

    private long lastUpdate;
    private long lastServerCacheUpdate;

    private Timer updateTimer;
    private ExecutorService threadPool;

    @PostConstruct
    private void init() {
        debug = env.getProperty("monitoring.debug_mode", boolean.class);
        if(debug) LOGGER.debug("Monitoring debug mode enabled.");
        timeout = env.getProperty("monitoring.connection_timeout_ms", int.class);
        nextPingDelay = env.getProperty("monitoring.next_ping_delay", int.class);
        serverCacheUpdatePeriod = env.getProperty("monitoring.server_cache_time", int.class);
        startMonitoring();
    }

    @PreDestroy
    private void stop() {
        LOGGER.info("Shutdown monitoring service...");
        updateTimer.cancel();
        try {
            threadPool.shutdown();
            while(!threadPool.awaitTermination(1, TimeUnit.SECONDS)) {}
        } catch (InterruptedException e) {
            LOGGER.catching(e);
        }
        LOGGER.info("Monitorind service terminated.");
    }

    private List<MonitoringServer> getServers() {
        long now = System.currentTimeMillis();
        if(now-lastServerCacheUpdate > serverCacheUpdatePeriod) {
            lastServerCacheUpdate = now;
            List<MonitoringServer> servers = new ArrayList<>();
            serverData.findAll().forEach(s->{
                if(s.isEnabled()) servers.add(s);
            });
            serverCache = servers;
        }
        return serverCache;
    }

    @Override
    public void markServersChanged() {
        lastServerCacheUpdate = 0;
    }

    private void startMonitoring() {
        threadPool = Executors.newCachedThreadPool();
        updateTimer = new Timer("monitoring_update");
        TimerTask updateTask = new TimerTask() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                List<MonitoringServer> servers = getServers();
                Set<String> validTags = servers.stream().map(s->s.getTag()).collect(Collectors.toSet());
                monitoringCache.keySet().removeIf(tag->!validTags.contains(tag));
                CountDownLatch lock = new CountDownLatch(servers.size());
                for(MonitoringServer server : servers) {
                    threadPool.execute(()->{
                        if(!server.isNowPinging()) {
                            MonitoringResult result = pingServer(server);
                            monitoringCache.put(server.getTag(), result);
                        }
                        lock.countDown();
                    });
                }
                try {
                    lock.await();
                    long end = System.currentTimeMillis();
                    lastUpdate = end;
                    long time = end-start;
                    if(debug) {
                        LOGGER.debug("Monitoring calculated for "+String.format("%1.3fs", time/1000.0F));
                    }
                } catch (InterruptedException e) {
                    LOGGER.catching(e);
                }
            }

        };
        updateTimer.schedule(updateTask, 0, nextPingDelay);
        LOGGER.info("Monitorind service started.");
    }

    @Override
    public long getLastUpdateTime() {
        return lastUpdate;
    }

    @Override
    public Map<String, MonitoringResult> getMonitoringMap(){
        return new HashMap<>(monitoringCache);
    }

    @Override
    public List<MonitoringResult> getMonitoringList(){
        List<MonitoringResult> list = new ArrayList<>(monitoringCache.values());
        Collections.sort(list, (a, b)->Integer.compare(a.getOrdinary(), b.getOrdinary()));
        return list;
    }

    @Override
    public MonitoringResult pingServer(MonitoringServer server) {
        MonitoringResultImpl result = new MonitoringResultImpl(server);
        server.setNowPinging(true);
        long start = System.currentTimeMillis();
        try (Socket socket = new Socket()){

            socket.connect(new InetSocketAddress(server.getIp(), server.getPort()), timeout);
            OutputStream out = socket.getOutputStream();

            //HANDSHAKE START
            ByteArrayOutputStream dataBytes = new ByteArrayOutputStream();
            DataOutputStream data = new DataOutputStream(dataBytes);
            data.writeByte(0);
            writeVarInt(data, 4);
            writeVarInt(data, server.getIp().length());
            data.writeBytes(server.getIp());
            data.writeShort(server.getPort());
            writeVarInt(data, 1);

            ByteArrayOutputStream dataBytes1 = new ByteArrayOutputStream();
            DataOutputStream data1 = new DataOutputStream(dataBytes1);
            writeVarInt(data1, dataBytes.size());
            data1.write(dataBytes.toByteArray());

            dataBytes1.writeTo(out);
            out.flush();
            //HANDSHAKE END

            //STATUS REQUEST START
            out.write(new byte[] {0x1, 0x0});
            out.flush();
            //STATUS REQUEST END

            //READING DATA
            DataInputStream response = new DataInputStream(socket.getInputStream());
            readVarInt(response);
            int packetId = readVarInt(response);
            if(packetId == 0) {
                int dataSize = readVarInt(response);
                if(dataSize > 0) {
                    byte[] receivedDataBytes = new byte[dataSize];
                    response.readFully(receivedDataBytes);
                    String receivedJson = new String(receivedDataBytes, StandardCharsets.UTF_8);
                    if(debug) LOGGER.debug("Server "+server.getTag()+" returned data: "+receivedJson);
                    ObjectMapper mapper = new ObjectMapper();
                    Map<?, ?> jsonRoot = (Map<?, ?>)mapper.readValue(receivedJson, Map.class);
                    String motd = (String) ((Map<?, ?>)jsonRoot.get("description")).get("text");
                    Map<?, ?> jsonPlayers = (Map<?, ?>) jsonRoot.get("players");
                    int currentPlayers = (int) jsonPlayers.get("online");
                    int maxPlayers = (int) jsonPlayers.get("max");
                    result = new MonitoringResultImpl(server, currentPlayers, maxPlayers, motd);
                    if(debug) {
                        LOGGER.debug("Server "+server.getTag()
                            +" monitoring result : "+result.toString());
                    }
                }else {
                    if(debug) {
                        LOGGER.warn("Server "+server.getTag()+" returned packet with wrong data size ("+dataSize+")");
                    }
                }
            }else {
                if(debug) {
                    LOGGER.warn("Server "+server.getTag()+" returned packet with wrong id. Expected '0', but received '"+packetId+"'");
                }
            }

        }catch (Exception ex) {
            if(debug) {
                LOGGER.warn("An exception while pinging server "+server.getTag());
                LOGGER.catching(ex);
            }
        }
        result.setPingTime((int)(System.currentTimeMillis()-start));
        server.setNowPinging(false);
        return result;
    }

    //write int without zeros
    private static void writeVarInt(DataOutputStream out, int val) throws IOException {
        while (true) {
            if ((val & 0xFFFFFF80) == 0) {
                out.writeByte(val);
                return;
            }
            out.writeByte(val & 0x7F | 0x80);
            val >>>= 7;
        }
    }

    private static int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
            if ((k & 0x80) != 0x80) {
                break;
            }
        }
        return i;
    }

    public static class MonitoringResultImpl implements MonitoringService.MonitoringResult{

        private String tag;
        private String name;
        private String description;
        private boolean online;
        private int currentPlayers, maxPlayers;
        private String motd;

        private transient int ordinary;
        private transient int pingTime = 0;

        private MonitoringResultImpl(MonitoringServer server) {
            this.tag = server.getTag();
            this.name = server.getName();
            this.description = server.getDescription();
            this.ordinary = server.getOrdinary();
        }

        private MonitoringResultImpl(MonitoringServer server, int currentPlayers,
                int maxPlayers, String motd) {
            this(server);
            this.online = true;
            this.currentPlayers = currentPlayers;
            this.maxPlayers = maxPlayers;
            this.motd = motd;
        }

        @Override
        public boolean isOnline() {
            return online;
        }

        @Override
        public String getServerTag() {
            return tag;
        }

        @Override
        public String getServerName() {
            return name;
        }

        @Override
        public String getServerDescription() {
            return description;
        }

        @Override
        public int getCurrentPlayers() {
            return currentPlayers;
        }

        @Override
        public int getMaxPlayers() {
            return maxPlayers;
        }

        @Override
        public String getMotd() {
            return motd;
        }

        public void setPingTime(int pingTime) {
            this.pingTime = pingTime;
        }

        @Override
        public int getPingTime() {
            return pingTime;
        }

        @Override
        public int getOrdinary() {
            return ordinary;
        }

        @Override
        public String toString() {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.writeValueAsString(this);
            } catch (JsonProcessingException e) {
                LOGGER.catching(e);
                return super.toString();
            }
        }

    }

}
