package org.example.ufc_api.service.impl;

import org.example.ufc_api.dto.ResultadoDto;
import org.example.ufc_api.model.Resultado;
import org.example.ufc_api.model.Pelea;
import org.example.ufc_api.model.Luchador;
import org.example.ufc_api.repository.ResultadoRepository;
import org.example.ufc_api.repository.PeleaRepository;
import org.example.ufc_api.repository.LuchadorRepository;
import org.example.ufc_api.service.ResultadoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResultadoServiceImpl implements ResultadoService {

    @Autowired
    private ResultadoRepository resultadoRepo;

    @Autowired
    private PeleaRepository peleaRepo;

    @Autowired
    private LuchadorRepository luchadorRepo;

    @Autowired
    private ModelMapper mapper;

    @Override
    public ResultadoDto create(ResultadoDto dto) {
        // Mapear DTO a entidad
        Resultado res = new Resultado();
        Pelea pelea = peleaRepo.findById(dto.getPeleaId())
                .orElseThrow(() -> new RuntimeException("Pelea no encontrada"));
        Luchador ganador = luchadorRepo.findById(dto.getGanadorId())
                .orElseThrow(() -> new RuntimeException("Luchador ganador no encontrado"));

        res.setPelea(pelea);
        res.setGanador(ganador);
        res.setMetodo(dto.getMetodo());
        res.setRoundFinal(dto.getRoundFinal());
        res.setTiempoFinal(dto.getTiempoFinal());

        Resultado saved = resultadoRepo.save(res);
        // Mapear entidad a DTO
        ResultadoDto out = mapper.map(saved, ResultadoDto.class);
        // Ajustar campos de FK
        out.setPeleaId(saved.getPelea().getId());
        out.setGanadorId(saved.getGanador().getId());
        return out;
    }

    @Override
    public List<ResultadoDto> findAll() {
        return resultadoRepo.findAll().stream().map(r -> {
            ResultadoDto dto = mapper.map(r, ResultadoDto.class);
            dto.setPeleaId(r.getPelea().getId());
            dto.setGanadorId(r.getGanador().getId());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public ResultadoDto findById(Long id) {
        Resultado r = resultadoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Resultado no encontrado"));
        ResultadoDto dto = mapper.map(r, ResultadoDto.class);
        dto.setPeleaId(r.getPelea().getId());
        dto.setGanadorId(r.getGanador().getId());
        return dto;
    }

    @Override
    public ResultadoDto update(Long id, ResultadoDto dto) {
        Resultado r = resultadoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Resultado no encontrado"));

        Pelea pelea = peleaRepo.findById(dto.getPeleaId())
                .orElseThrow(() -> new RuntimeException("Pelea no encontrada"));
        Luchador ganador = luchadorRepo.findById(dto.getGanadorId())
                .orElseThrow(() -> new RuntimeException("Luchador ganador no encontrado"));

        r.setPelea(pelea);
        r.setGanador(ganador);
        r.setMetodo(dto.getMetodo());
        r.setRoundFinal(dto.getRoundFinal());
        r.setTiempoFinal(dto.getTiempoFinal());

        Resultado updated = resultadoRepo.save(r);
        ResultadoDto out = mapper.map(updated, ResultadoDto.class);
        out.setPeleaId(updated.getPelea().getId());
        out.setGanadorId(updated.getGanador().getId());
        return out;
    }

    @Override
    public void delete(Long id) {
        resultadoRepo.deleteById(id);
    }
}
