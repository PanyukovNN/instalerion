package org.union.common.service;

import com.github.kilianB.hashAlgorithms.DifferenceHash;
import com.github.kilianB.hashAlgorithms.PerceptiveHash;
import com.github.kilianB.matcher.persistent.database.H2DatabaseImageMatcher;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ImageIndexer {

    private final Map<Long, H2DatabaseImageMatcher> databases = new HashMap<>(5);
    private final DifferenceHash differenceHash = new DifferenceHash(32, DifferenceHash.Precision.Double);
    private final PerceptiveHash perceptiveHash = new PerceptiveHash(32);

    // TODO processImage

    private H2DatabaseImageMatcher getDatabaseForChannel(Long channelId) throws SQLException {
        H2DatabaseImageMatcher db = databases.get(channelId);

        if (db != null) {
            return db;
        }

        String jdbcUrl = "jdbc:h2:./imagesdb_" + channelId;
        Connection conn = DriverManager.getConnection(jdbcUrl, "root", "");
        db = new H2DatabaseImageMatcher(conn);
        db.addHashingAlgorithm(differenceHash, 0.4);
        db.addHashingAlgorithm(perceptiveHash, 0.2);
        databases.put(channelId, db);
        return db;
    }
}
