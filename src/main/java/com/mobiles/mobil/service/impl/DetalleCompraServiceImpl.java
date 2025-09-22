package com.mobiles.mobil.service.impl;

import java.util.List;

import org.hibernate.service.spi.ServiceException;

import com.mobiles.mobil.model.Dto.DetalleCompraDTO;
import com.mobiles.mobil.model.mapper.DetalleCompraMapper;
import com.mobiles.mobil.repository.DetalleCompraRepository;
import com.mobiles.mobil.service.service.DetalleCompraService;

public class DetalleCompraServiceImpl implements DetalleCompraService {

   private final DetalleCompraMapper detalleCompraMapper;
   private final DetalleCompraRepository detalleCompraRepository;

    public DetalleCompraServiceImpl(DetalleCompraMapper detalleCompraMapper, DetalleCompraRepository detalleCompraRepository) {
        this.detalleCompraMapper = detalleCompraMapper;
        this.detalleCompraRepository = detalleCompraRepository;
    }


    @Override
    public DetalleCompraDTO create(DetalleCompraDTO dto) throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public DetalleCompraDTO update(Long id, DetalleCompraDTO dto) throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public DetalleCompraDTO findById(Long id) throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public void deleteById(Long id) throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
    }

    @Override
    public List<DetalleCompraDTO> findAll() throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

}
