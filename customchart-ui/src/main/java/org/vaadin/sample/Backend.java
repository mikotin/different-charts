package org.vaadin.sample;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Simplified mock-up of "backend"
 *
 * Just an object that gives the needed data with raw sql-queries
 */
public class Backend {
    private DataSource dataSource;

    private Statement stmt = null;

    public Backend(DataSource ds) {
        this.dataSource = ds;
        try {
            this.stmt = ds.getConnection().createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Get countries available in source data
     *
     * @return list of countries
     */
    public List<String> getAvailableCountries() {
        ArrayList<String> countries = new ArrayList<>();
        String query = "SELECT \"name\" FROM \"country\" ORDER BY \"name\" ASC";
        try {
            ResultSet rs = stmt.executeQuery(query);

            while(rs.next()) {
                countries.add(rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return countries;
    }

    /**
     * Returns 2-level grouped value set grouped by (higher level) either country or specie with value of population or avarage income
     * For instance grouping by specie for value of population, might return something like:
     * HashMap<String, HashMap<String, Number>>
     *     {
     *         "Orc" => {
     *             "Orcland" => 1242530,
     *             "Elfland" => 52515
     *         },
     *         "Elf" => {
     *             "Orcland" => 2145,
     *             "Elfland" => 351534
     *         }
     *     }
     * @param group
     * @param value
     * @return
     */
    public HashMap<String, HashMap<String, Number>> getDataSet(groupType group, valueType value) {
        HashMap<String, HashMap<String, Number>> result = new HashMap<>();

        String query = "SELECT c.\"name\" AS country, s.\"name\" AS specie, \"population\", \"avarage_income\"\n" +
                "FROM \"country\" AS c\n" +
                "JOIN \"inhabit\" AS i ON i.\"country_id\" = c.\"id\"\n" +
                "JOIN \"specie\" AS s ON s.\"id\" = i.\"specie_id\"\n" +
                "ORDER BY c.\"name\", s.\"name\"";
        String keyGet = (group.equals(groupType.COUNTRY) ? "country" : "specie");
        String subKeyGet = (group.equals(groupType.COUNTRY) ? "specie" : "country");
        String valGet = (value.equals(valueType.INCOME) ? "avarage_income" : "population");

        String key;
        String subKey;
        int val;
        try {
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()) {
                key = rs.getString(keyGet);
                subKey = rs.getString(subKeyGet);
                val = rs.getInt(valGet);

                if (!result.containsKey(key)) {
                    result.put(key, new HashMap<>());
                }
                if (!result.get(key).containsKey(subKey)) {
                    result.get(key).put(subKey, val);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public enum groupType {
        COUNTRY,
        SPECIE
    }

    public enum valueType {
        POPULATION,
        INCOME
    }
}
