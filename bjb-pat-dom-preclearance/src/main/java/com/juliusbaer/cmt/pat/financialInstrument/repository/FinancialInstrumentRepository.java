package com.juliusbaer.cmt.pat.financialInstrument.repository;


import com.juliusbaer.cmt.pat.financialInstrument.dto.FinancialInstrumentDropdownDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FinancialInstrumentRepository extends JpaRepository<com.juliusbaer.cmt.pat.financialInstrument.entity.FinancialInstrument, String> {


    @Query("""
                SELECT new  com.juliusbaer.cmt.pat.financialInstrument.dto.FinancialInstrumentDropdownDto(
                    f.assBjbNameLongIssuer,
                    f.assIsin,
                    f.assValorNo,
                    f.assBbgid,
                    f.assJbGlobalId
                )
                FROM FinancialInstrument f
                WHERE (:name IS NULL OR LOWER(f.assBjbNameLongIssuer) LIKE LOWER(CONCAT(:name, '%')))
                   OR (:isin IS NULL OR LOWER(f.assIsin) LIKE LOWER(CONCAT(:isin, '%')))
                   OR (:valorNo IS NULL OR LOWER(f.assValorNo) LIKE LOWER(CONCAT(:valorNo, '%')))
            """)
    Page<FinancialInstrumentDropdownDto> searchAny(@Param("name") String name, @Param("isin") String isin, @Param("valorNo") String valorNo, Pageable pageable);

    @Query("""
                SELECT new  com.juliusbaer.cmt.pat.financialInstrument.dto.FinancialInstrumentDropdownDto(
                    f.assBjbNameLongIssuer,
                    f.assIsin,
                    f.assValorNo,
                    f.assBbgid,
                    f.assJbGlobalId
                )
                FROM MvInstrumentsInstrumentenEquities f
                WHERE (:name IS NULL OR LOWER(f.assBjbNameLongIssuer) LIKE LOWER(CONCAT(:name, '%')))
                   OR (:isin IS NULL OR LOWER(f.assIsin) LIKE LOWER(CONCAT(:isin, '%')))
                   OR (:valorNo IS NULL OR LOWER(f.assValorNo) LIKE LOWER(CONCAT(:valorNo, '%')))
            """)
    Page<FinancialInstrumentDropdownDto> searchEquities(@Param("name") String name, @Param("isin") String isin, @Param("valorNo") String valorNo, Pageable pageable);

    @Modifying
    @Query("DELETE FROM FinancialInstrument")
    void deleteAll();


    @Query("""
                SELECT new  com.juliusbaer.cmt.pat.financialInstrument.dto.FinancialInstrumentDropdownDto(
                    f.assBjbNameLongIssuer,
                    f.assIsin,
                    f.assValorNo,
                    f.assBbgid,
                    f.assJbGlobalId
                )
                FROM MvInstrumentsInstrumentenAllNotOptions f
                WHERE (:name IS NULL OR LOWER(f.assBjbNameLongIssuer) LIKE LOWER(CONCAT(:name, '%')))
                   OR (:isin IS NULL OR LOWER(f.assIsin) LIKE LOWER(CONCAT(:isin, '%')))
                   OR (:valorNo IS NULL OR LOWER(f.assValorNo) LIKE LOWER(CONCAT(:valorNo, '%')))
            """)
    Page<FinancialInstrumentDropdownDto> searchAllNoOptions(@Param("name") String name, @Param("isin") String isin, @Param("valorNo") String valorNo, Pageable pageable);
}
