package dmit2015.repository;

import dmit2015.entity.Country;
import jakarta.data.repository.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository {
    @Query("select c from Country c join fetch c.region order by c.countryName")
    List<Country> findAll();

    @Query("select c from Country c join fetch c.region where c.countryId = ?1 order by c.countryName")
    Optional<Country> findById(String countryId);

    @Insert
    void add(Country newCountry);

    @Update
    void update(Country existingCountry);

    @Delete
    void delete(Country existingCountry);
}
