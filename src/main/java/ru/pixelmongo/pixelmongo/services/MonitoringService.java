package ru.pixelmongo.pixelmongo.services;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.pixelmongo.pixelmongo.data.MonitoringServer;
import ru.pixelmongo.pixelmongo.repositories.MonitoringServerRepository;

@Service
public class MonitoringService {
	
	public static final Logger LOGGER = LogManager.getLogger("Monitoring");

	@Autowired
	private MonitoringServerRepository serverData;
	
	@Autowired
	private Environment env;
	
	private boolean debug;
	
	private int timeout;
	
	private int nextPingDelay;
	
	private Map<String, MonitoringResult> monitoringCache = new ConcurrentHashMap<>();
	
	private long lastUpdate;
	
	private Thread rootThread;
	private ExecutorService threadPool;
	
	private boolean working = true;
	
	@PostConstruct
	private void init() {
		debug = env.getProperty("monitoring.debug_mode", boolean.class);
		if(debug) LOGGER.info("Monitoring debug mode enabled.");
		timeout = env.getProperty("monitoring.connection_timeout_ms", int.class);
		nextPingDelay = env.getProperty("monitoring.next_ping_delay", int.class);
		startMonitoring();
	}
	
	@PreDestroy
	private void stop() {
		working = false;
	}
	
	private void startMonitoring() {
		threadPool = Executors.newCachedThreadPool();
		rootThread = new Thread(()->{
			while(working) {
				long start = System.currentTimeMillis();
				List<MonitoringServer> servers = new ArrayList<>();
				List<String> invalidTags = new ArrayList<>(monitoringCache.keySet());
				for(MonitoringServer server : serverData.findAll()) {
					servers.add(server);
					invalidTags.remove(server.getTag());
				}
				for(String tag : invalidTags) {
					monitoringCache.remove(tag);
				}
				CountDownLatch lock = new CountDownLatch(servers.size());
				for(MonitoringServer server : servers) {
					threadPool.execute(()->{
						MonitoringResult result = pingServer(server);
						monitoringCache.put(server.getTag(), result);
						lock.countDown();
					});
				}
				try {
					lock.await();
					long end = System.currentTimeMillis();
					lastUpdate = end;
					long time = end-start;
					if(debug) {
						LOGGER.info("Monitoring calculated for "+String.format("%1.3fs", time/1000.0F));
					}
					Thread.sleep(Math.max(Math.min(nextPingDelay/3, 1000),
							nextPingDelay-(time)));
				} catch (InterruptedException e) {
					LOGGER.catching(e);
				}
			}
		}, "monitoring_root");
		rootThread.start();
	}
	
	public long getLastUpdateTime() {
		return lastUpdate;
	}
	
	public Map<String, MonitoringResult> getMonitoringMap(){
		return new HashMap<>(monitoringCache);
	}
	
	public List<MonitoringResult> getMonitoringList(){
		return new ArrayList<>(monitoringCache.values());
	}
	
	public long getLastUpdate() {
		return lastUpdate;
	}
	
	public MonitoringResult pingServer(MonitoringServer server) {
		MonitoringResult result = new MonitoringResult(server);
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
					if(debug) LOGGER.info("Server "+server.getTag()+" returned data: "+receivedJson);
					ObjectMapper mapper = new ObjectMapper();
					Map<?, ?> jsonRoot = (Map<?, ?>)mapper.readValue(receivedJson, Map.class);
					String motd = (String) ((Map<?, ?>)jsonRoot.get("description")).get("text");
					Map<?, ?> jsonPlayers = (Map<?, ?>) jsonRoot.get("players");
					int currentPlayers = (int) jsonPlayers.get("online");
					int maxPlayers = (int) jsonPlayers.get("max");
					result = new MonitoringResult(server, currentPlayers, maxPlayers, motd);

					if(debug) {
						System.out.println("Server "+server.getTag()
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
			
		} catch (UnknownHostException ex) {
			LOGGER.warn("An exception while pinging server "+server.getTag());
			LOGGER.catching(ex);
		} catch (IOException ex) {
			if(debug) {
				LOGGER.warn("An exception while pinging server "+server.getTag());
				LOGGER.catching(ex);
			}
		}
		server.setNowPinging(false);
		result.setPingTime((int)(System.currentTimeMillis()-start));
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
	
	public static class MonitoringResult {
		
		private String tag;
		private String name;
		private String description;
		private boolean online;
		private int currentPlayers, maxPlayers;
		private String motd;
		
		@JsonIgnore
		private transient int pingTime = 0;
		
		private MonitoringResult(MonitoringServer server) {
			this.tag = server.getTag();
			this.name = server.getName();
			this.description = server.getDescription();
		}
		
		private MonitoringResult(MonitoringServer server, int currentPlayers,
				int maxPlayers, String motd) {
			this(server);
			this.online = true;
			this.currentPlayers = currentPlayers;
			this.maxPlayers = maxPlayers;
			this.motd = motd;
		}
		
		public boolean isOnline() {
			return online;
		}
		
		@JsonProperty("tag")
		public String getServerTag() {
			return tag;
		}

		@JsonProperty("name")
		public String getServerName() {
			return name;
		}
		
		@JsonProperty("description")
		public String getServerDescription() {
			return description;
		}
		
		public int getCurrentPlayers() {
			return currentPlayers;
		}

		public int getMaxPlayers() {
			return maxPlayers;
		}

		public String getMotd() {
			return motd;
		}
		
		public void setPingTime(int pingTime) {
			this.pingTime = pingTime;
		}
		
		@JsonIgnore
		public int getPingTime() {
			return pingTime;
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
