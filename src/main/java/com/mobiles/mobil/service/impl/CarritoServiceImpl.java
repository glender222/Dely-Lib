package com.mobiles.mobil.service.impl;

import java.util.List;

import org.hibernate.service.spi.ServiceException;

import com.mobiles.mobil.model.Dto.CarritoDTO;
import com.mobiles.mobil.model.mapper.CarritoMapper;
import com.mobiles.mobil.repository.CarritoRepository;
import com.mobiles.mobil.service.service.CarritoService;

public class CarritoServiceImpl implements CarritoService{
   private final CarritoMapper carritoMapper;
   private final CarritoRepository carritoRepository;

   
    public CarritoServiceImpl(CarritoMapper carritoMapper, CarritoRepository carritoRepository) {
        this.carritoMapper = carritoMapper;
        this.carritoRepository = carritoRepository;
    }


    @Override
    public CarritoDTO create(CarritoDTO dto) throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public CarritoDTO update(Long id, CarritoDTO dto) throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public CarritoDTO findById(Long id) throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public void deleteById(Long id) throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
    }

    @Override
    public List<CarritoDTO> findAll() throws ServiceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

}
