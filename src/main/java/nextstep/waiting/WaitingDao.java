package nextstep.waiting;

import nextstep.member.Member;
import nextstep.schedule.Schedule;
import nextstep.theme.Theme;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;

@Component
public class WaitingDao {

    private final JdbcTemplate jdbcTemplate;

    public WaitingDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Waiting> rowMapper = (resultSet, rowNum) -> new Waiting(
            resultSet.getLong("waiting.id"),
            new Schedule(
                    resultSet.getLong("schedule.id"),
                    new Theme(
                            resultSet.getLong("theme.id"),
                            resultSet.getString("theme.name"),
                            resultSet.getString("theme.desc"),
                            resultSet.getInt("theme.price")
                    ),
                    resultSet.getDate("schedule.date").toLocalDate(),
                    resultSet.getTime("schedule.time").toLocalTime()
            ),
            new Member(
                    resultSet.getLong("member.id"),
                    resultSet.getString("member.username"),
                    resultSet.getString("member.password"),
                    resultSet.getString("member.name"),
                    resultSet.getString("member.phone"),
                    resultSet.getString("member.role")
            )
    );
    
    public Waiting findById(Long id) {
        String sql = "SELECT " +
                "waiting.id, waiting.schedule_id, waiting.member_id, " +
                "schedule.id, schedule.theme_id, schedule.date, schedule.time, " +
                "theme.id, theme.name, theme.desc, theme.price, " +
                "member.id, member.username, member.password, member.name, member.phone, member.role " +
                "FROM waiting " +
                "INNER JOIN schedule ON waiting.schedule_id = schedule.id " +
                "INNER JOIN theme ON schedule.theme_id = theme.id " +
                "INNER JOIN member ON waiting.member_id = member.id " +
                "WHERE waiting.id = ?;";
        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, id);
        } catch (Exception e) {
            return null;
        }
    }

    public void deleteById(Long id) {
        String sql = "Delete FROM waiting WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public Long save(Waiting waiting) {
        String sql = "INSERT INTO waiting (schedule_id, member_id) VALUES (?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, waiting.getSchedule().getId());
            ps.setLong(2, waiting.getMember().getId());
            return ps;

        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public List<Waiting> findByMemberId(Long memberId) {
        String sql = "SELECT " +
                "waiting.id, waiting.schedule_id, waiting.member_id, " +
                "schedule.id, schedule.theme_id, schedule.date, schedule.time, " +
                "theme.id, theme.name, theme.desc, theme.price, " +
                "member.id, member.username, member.password, member.name, member.phone, member.role " +
                "FROM waiting " +
                "INNER JOIN schedule ON waiting.schedule_id = schedule.id " +
                "INNER JOIN theme ON schedule.theme_id = theme.id " +
                "INNER JOIN member ON waiting.member_id = member.id " +
                "WHERE member.id = ?;";

        try {
            return jdbcTemplate.query(sql, rowMapper, memberId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public Long getWaitNum(Waiting waiting) {
        String sql = "SELECT " +
                "count(*) " +
                "FROM waiting " +
                "WHERE waiting.id < ? AND waiting.schedule_id = ?";

        Long count = jdbcTemplate.queryForObject(sql, Long.class, waiting.getId(), waiting.getSchedule().getId());
        return count + 1;
    }

    public List<Waiting> findByScheduleId(Long scheduleId) {
        String sql = "SELECT " +
                "waiting.id, waiting.schedule_id, waiting.member_id, " +
                "schedule.id, schedule.theme_id, schedule.date, schedule.time, " +
                "theme.id, theme.name, theme.desc, theme.price, " +
                "member.id, member.username, member.password, member.name, member.phone, member.role " +
                "FROM waiting " +
                "INNER JOIN schedule ON waiting.schedule_id = schedule.id " +
                "INNER JOIN theme ON schedule.theme_id = theme.id " +
                "INNER JOIN member ON waiting.member_id = member.id " +
                "WHERE schedule.id = ?;";

        try {
            return jdbcTemplate.query(sql, rowMapper, scheduleId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public Waiting findFirstWaitingByScheduleId(Long scheduleId) {
        String sql = "SELECT " +
                "waiting.id, waiting.schedule_id, waiting.member_id, " +
                "schedule.id, schedule.theme_id, schedule.date, schedule.time, " +
                "theme.id, theme.name, theme.desc, theme.price, " +
                "member.id, member.username, member.password, member.name, member.phone, member.role " +
                "FROM waiting " +
                "INNER JOIN schedule ON waiting.schedule_id = schedule.id " +
                "INNER JOIN theme ON schedule.theme_id = theme.id " +
                "INNER JOIN member ON waiting.member_id = member.id " +
                "WHERE schedule.id = ? ORDER BY waiting.id LIMIT 1;";

        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, scheduleId);
        } catch (Exception e) {
            return null;
        }
    }
}
