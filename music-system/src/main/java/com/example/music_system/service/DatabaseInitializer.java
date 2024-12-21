package com.example.music_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DatabaseInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initDatabaseFunctions() {
        try {
            jdbcTemplate.execute(
                    "CREATE OR REPLACE FUNCTION find_descriptions_by_prefix(prefix TEXT) " +
                            "RETURNS TABLE(id INTEGER, description TEXT) AS $$ " +
                            "BEGIN " +
                            "RETURN QUERY " +
                            "SELECT music_band.id, music_band.description::TEXT " + // Указываем полное имя таблицы
                            "FROM music_band " +
                            "WHERE music_band.description LIKE prefix || '%'; " +
                            "END; " +
                            "$$ LANGUAGE plpgsql;"
            );
            logger.info("Function find_descriptions_by_prefix created successfully.");
            jdbcTemplate.execute(
                    "CREATE OR REPLACE FUNCTION add_single_to_band(band_id INTEGER, singles_to_add_param INTEGER) " + // Переименован параметр
                            "RETURNS VOID AS $$ " +
                            "BEGIN " +
                            "UPDATE music_band " +
                            "SET singles_count = music_band.singles_count + singles_to_add_param " + // Явное указание колонки и уникальное имя параметра
                            "WHERE music_band.id = band_id; " +
                            "END; " +
                            "$$ LANGUAGE plpgsql;"
            );
            logger.info("Function add_single_to_band created successfully.");

            jdbcTemplate.execute(
                    "CREATE OR REPLACE FUNCTION add_participant_to_band(band_id INTEGER, participants_to_add INTEGER) " +
                            "RETURNS VOID AS $$ " +
                            "BEGIN " +
                            "UPDATE music_band " +
                            "SET number_of_participants = number_of_participants + participants_to_add " + // Указываем полное имя поля
                            "WHERE id = band_id; " +
                            "END; " +
                            "$$ LANGUAGE plpgsql;"
            );
            logger.info("Function add_participant_to_band created successfully.");

        } catch (Exception e) {
            logger.error("Error initializing database functions", e);
        }
    }
}
