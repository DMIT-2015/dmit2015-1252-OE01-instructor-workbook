package dmit2015.repository;

import dmit2015.entity.Region;
import jakarta.data.repository.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionRepository {
    @Find
    List<Region> findAll();

    @Find
    Optional<Region> findById(Long id);

    @Insert
    void add(Region newRegion);

    @Update
    void update(Region existingRegion);

    @Delete
    void delete(Region existingRegion);
}
