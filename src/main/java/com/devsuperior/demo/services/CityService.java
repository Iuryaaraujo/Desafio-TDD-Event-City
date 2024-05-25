package com.devsuperior.demo.services;

import com.devsuperior.demo.dto.CityDTO;
import com.devsuperior.demo.entities.City;
import com.devsuperior.demo.repositories.CityRepository;
import com.devsuperior.demo.services.exceptions.DataBaseException;
import com.devsuperior.demo.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CityService {

    @Autowired
    private CityRepository repository;

    //find All deve retornar todos os recursos classificados por nome
    @Transactional(readOnly = true)
    public List<CityDTO> findAll() {

        List<City> list = repository.findAll(Sort.by("name"));

        return list.stream().map(x -> new CityDTO(x)).collect(Collectors.toList());
    }

    // inserir deve inserir recurso
    @Transactional
    public CityDTO insert(CityDTO dto) {

        City entity = new City();
        convertDtoToEntity(dto, entity);
        entity = repository.save(entity);
        return new CityDTO(entity);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }

        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseException("Falha de integridade referencial");
        }

    }

    private void convertDtoToEntity(CityDTO dto, City entity) {
        entity.setName(dto.getName());
    }
}
