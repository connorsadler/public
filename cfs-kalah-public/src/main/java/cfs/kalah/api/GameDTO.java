package cfs.kalah.api;

import java.util.Map;
import java.util.Objects;

public class GameDTO {
    
    private String id;
    private String url;
    private Map<String, String> status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getStatus() {
        return status;
    }

    public void setStatus(Map<String, String> status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameDTO gameDTO = (GameDTO) o;
        return Objects.equals(id, gameDTO.id) &&
                Objects.equals(url, gameDTO.url) &&
                Objects.equals(status, gameDTO.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, status);
    }
}
