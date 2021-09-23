package ru.pixelmongo.pixelmongo.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import ru.pixelmongo.pixelmongo.model.dao.OrderedData;
import ru.pixelmongo.pixelmongo.repositories.OrderedDataRepository;
import ru.pixelmongo.pixelmongo.services.OrdinaryUtilsService;

@Service("ordinaryUtils")
public class OrdinaryUtilsServiceImpl implements OrdinaryUtilsService{

    public <ID> int reorder(OrderedDataRepository<? extends OrderedData<ID>, ID> repo,
            Iterable<? extends OrderedData<ID>> data, List<ID> orderedIds) {
        if(orderedIds.isEmpty()) return 0;
        Map<ID, OrderedData<ID>> dataMap = new HashMap<>();
        data.forEach(od->dataMap.put(od.getId(), od));
        List<OrderedData<ID>> changed = new ArrayList<>();
        for(int i = 0; i < orderedIds.size(); i++) {
            OrderedData<ID> od = dataMap.get(orderedIds.get(i));
            if(od != null && od.getOrdinary() != i+1) {
                od.setOrdinary(i+1);
                changed.add(od);
            }
        }
        changed.forEach(repo::updateOrdinary);
        return changed.size();
    }

}
