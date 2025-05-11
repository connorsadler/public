package cfs.kalah.api;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

public class CreateGameResponseDTO {
    
    private String id;
    private String uri;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateGameResponseDTO that = (CreateGameResponseDTO) o;
        return id.equals(that.id) &&
                uri.equals(that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uri);
    }
}
