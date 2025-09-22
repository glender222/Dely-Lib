package com.mobiles.mobil.service.impl;

import java.util.List;

import org.hibernate.service.spi.ServiceException;

import com.mobiles.mobil.model.Dto.GeneroLibroDTO;
import com.mobiles.mobil.model.mapper.GeneroLibroMapper;
import com.mobiles.mobil.repository.GeneroLibroRepository;
import com.mobiles.mobil.service.service.GeneroLibroService;

public class GeneroLibroServiceImpl implements GeneroLibroService {

private final GeneroLibroMapper generoLibroMapper;
private final GeneroLibroRepository generoLibroRepository;

    public GeneroLibroServiceImpl(GeneroLibroMapper generoLibroMapper, GeneroLibroRepository generoLibroRepository) {
        this.generoLibroMapper = generoLibroMapper;
        this.generoLibroRepository = generoLibroRepository;
    }


    @Override
    public GeneroLibroDTO create(GeneroLibroDTO dto) throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public GeneroLibroDTO update(Long id, GeneroLibroDTO dto) throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public GeneroLibroDTO findById(Long id) throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public void deleteById(Long id) throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
    }

    @Override
    public List<GeneroLibroDTO> findAll() throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

}
