package se.rgz.models.Requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class SetValueRequest {
    @NonNull
    public Integer row;
    @NonNull
    public Integer column;
    public String value;
}
