
public class ConsultasRepository extends JpaRepository{

    // Consulta com filtro e pageable entre entidades ManyToMany
    @Query(value = "SELECT s FROM Sale s " +
            "JOIN FETCH s.seller " +
            "WHERE s.date >= :minDate " +
            "AND s.date <= :maxDate " +
            "AND UPPER(s.seller.name) " +
            "LIKE UPPER(CONCAT('%', :name, '%'))",
            countQuery = "SELECT COUNT(s) FROM Sale s " +
                    "JOIN s.seller " +
                    "WHERE s.date >= :minDate " +
                    "AND s.date <= :maxDate " +
                    "AND UPPER(s.seller.name) " +
                    "LIKE UPPER(CONCAT('%', :name, '%'))"
    )
    Page<Sale> searchAllWithSeller(@Param("minDate") LocalDate minDate,
                                   @Param("maxDate") LocalDate maxDate,
                                   @Param("name") String name,
                                   Pageable pageable);



    // Consulta para Soma de valores com filtro e pageable entre entidades ManyToMany
    @Query(value = "SELECT s.name AS name, COALESCE(SUM(sa.amount), 0) AS totalAmount " +
            "FROM Seller s " +
            "JOIN s.sales sa " +
            "WHERE sa.date >= :minDate " +
            "AND sa.date <= :maxDate " +
            "GROUP BY s.name ",
            countQuery = "SELECT COUNT(DISTINCT s.id) " +
                    "FROM Seller s " +
                    "JOIN s.sales sa " +
                    "WHERE sa.date >= :minDate " +
                    "AND sa.date <= :maxDate "
    )
    Page<SellerSumaryProjection> searchSumary (@Param("minDate")LocalDate minDate,
                                               @Param("maxDate") LocalDate maxDate,
                                               Pageable pageable);






    // Busca ManyToMany completa - alternativa 1
    @Query(value = "SELECT DISTINCT p FROM Perfil p " +
            "LEFT JOIN FETCH p.usuarios")
    List<Perfil> searchAllWithUsuarios();

    // Busca ManyToMany completa - alternativa 2
    @EntityGraph(attributePaths = "usuarios")
    @Query("SELECT p FROM Perfil p")
    List<Perfil> searchAllWithUsuarios();

}