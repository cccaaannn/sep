package com.kurtcan.sepaggregatorservice.shared.pagaination;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PageMapper {

    private final ModelMapper mapper;

    public <D, S> PageImpl<D> mapPaginatedList(PageImpl<S> entities, Class<D> dtoClass) {
        return entities.map(objectEntity -> mapper.map(objectEntity, dtoClass));
    }
}
