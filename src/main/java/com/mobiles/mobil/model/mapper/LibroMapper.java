package com.mobiles.mobil.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.mobiles.mobil.model.Dto.LibroDTO;
import com.mobiles.mobil.model.entity.Libro;
import com.mobiles.mobil.model.mapper.Base.BaseMappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface LibroMapper extends BaseMappers<Libro, LibroDTO> {
}

