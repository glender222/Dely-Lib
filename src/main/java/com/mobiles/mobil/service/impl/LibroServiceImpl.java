package com.mobiles.mobil.service.impl;

import java.util.List;

import org.hibernate.service.spi.ServiceException;

import com.mobiles.mobil.model.Dto.LibroDTO;
import com.mobiles.mobil.model.entity.Libro;
import com.mobiles.mobil.model.mapper.LibroMapper;
import com.mobiles.mobil.repository.LibroRepository;
import com.mobiles.mobil.service.service.LibroService;

public class LibroServiceImpl implements LibroService{
 
  private final LibroMapper libroMapper; 
  private final LibroRepository libroRepository;

    public LibroServiceImpl(LibroMapper libroMapper, LibroRepository libroRepository) {
        this.libroMapper = libroMapper;
        this.libroRepository = libroRepository;
    }

    @Override
    public LibroDTO create(LibroDTO dto) throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public LibroDTO update(Long id, LibroDTO dto) throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public LibroDTO findById(Long id) throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public void deleteById(Long id) throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
    }

    @Override
    public List<LibroDTO> findAll() throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

}
