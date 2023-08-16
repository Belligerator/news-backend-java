package cz.belli.skodabackend.endpoint.tag;

import cz.belli.skodabackend.model.enumeration.LanguageEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;

@Getter
@Setter
public class TagEntityId implements Serializable {

    private String id;

    // @Column() is added so LanguageEntityConverter is applied
    @Column()
    private LanguageEnum language;

}
