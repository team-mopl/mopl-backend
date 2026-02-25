package mopl.persistence.rdb.global.dto;

import lombok.Builder;
import mopl.persistence.rdb.global.enums.SortDirection;

import java.util.List;

@Builder
public record PageResponse<T>(
        List<T> data,
        String nextCursor,
        Object nextIdAfter,
        boolean hasNext,
        long totalCount, // 0 고정값
        String sortBy,
        SortDirection sortDirection
) {
}
