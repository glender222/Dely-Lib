package com.mobiles.mobil.service.impl;

import java.util.List;

import org.hibernate.service.spi.ServiceException;

import com.mobiles.mobil.model.Dto.InventarioDTO;
import com.mobiles.mobil.model.mapper.InventarioMapper;
import com.mobiles.mobil.repository.InventarioRepository;
import com.mobiles.mobil.service.service.InventarioService;


public class InventarioServiceImpl implements InventarioService{
   private final InventarioRepository inventarioRepository;
   private final InventarioMapper inventarioMapper;

   public InventarioServiceImpl(InventarioRepository inventarioRepository, InventarioMapper inventarioMapper) {
       this.inventarioRepository = inventarioRepository;
       this.inventarioMapper = inventarioMapper;
   }

    @Override
    public InventarioDTO create(InventarioDTO dto) throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public InventarioDTO update(Long id, InventarioDTO dto) throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public InventarioDTO findById(Long id) throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public void deleteById(Long id) throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
    }

    @Override
    public List<InventarioDTO> findAll() throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

}
