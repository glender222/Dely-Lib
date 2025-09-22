package com.mobiles.mobil.service.impl;

import java.util.List;

import org.hibernate.service.spi.ServiceException;

import com.mobiles.mobil.model.Dto.CompraDTO;
import com.mobiles.mobil.model.mapper.CompraMapper;
import com.mobiles.mobil.repository.CompraRepository;
import com.mobiles.mobil.service.service.CompraService;

public class CompraServiceImpl implements CompraService{

   private final CompraMapper compraMapper;
   private final CompraRepository compraRepository;
    public CompraServiceImpl(CompraMapper compraMapper, CompraRepository compraRepository) {
        this.compraMapper = compraMapper;
        this.compraRepository = compraRepository;
    }

    @Override
    public CompraDTO create(CompraDTO dto) throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public CompraDTO update(Long id, CompraDTO dto) throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public CompraDTO findById(Long id) throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public void deleteById(Long id) throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
    }

    @Override
    public List<CompraDTO> findAll() throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

}
