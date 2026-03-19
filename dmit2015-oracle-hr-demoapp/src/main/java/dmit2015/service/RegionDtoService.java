package dmit2015.service;

import dmit2015.dto.RegionDto;

import java.util.List;
import java.util.Optional;

public interface RegionDtoService {

    RegionDto createRegionDto(RegionDto regionDto);

    Optional<RegionDto> getRegionDtoById(Long id);

    List<RegionDto> getAllRegionDtos();

    RegionDto updateRegionDto(RegionDto regionDto);

    void deleteRegionDtoById(Long id);
}