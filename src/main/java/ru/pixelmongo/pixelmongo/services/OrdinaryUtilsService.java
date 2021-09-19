package ru.pixelmongo.pixelmongo.services;

import java.util.List;

import ru.pixelmongo.pixelmongo.model.dao.OrderedData;
import ru.pixelmongo.pixelmongo.repositories.OrderedDataRepository;

public interface OrdinaryUtilsService {

    /**
     * Change ordinaries of given data collection according to given IDs sequence
     *
     * @param <ID>
     * @param repo
     * @param data
     * @param orderedIds
     * @return
     */
    public <ID> int reorder(OrderedDataRepository<? extends OrderedData<ID>, ID> repo,
            Iterable<? extends OrderedData<ID>> data, List<ID> orderedIds);

}
