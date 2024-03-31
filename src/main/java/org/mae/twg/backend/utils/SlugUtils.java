package org.mae.twg.backend.utils;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.mae.twg.backend.exceptions.SlugException;
import org.mae.twg.backend.models.Model;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.Local;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SlugUtils {

    private static final Map<String, String> RUToEN = Map.ofEntries(
            Map.entry("а", "a"), Map.entry("б", "b"), Map.entry("в", "v"),
            Map.entry("г", "g"), Map.entry("д", "d"), Map.entry("е", "e"),
            Map.entry("ё", "yo"), Map.entry("ж", "j"), Map.entry("з", "z"),
            Map.entry("и", "i"), Map.entry("й", "y"), Map.entry("к", "k"),
            Map.entry("л", "l"), Map.entry("м", "m"), Map.entry("н", "n"),
            Map.entry("о", "o"), Map.entry("п", "p"), Map.entry("р", "r"),
            Map.entry("с", "s"), Map.entry("т", "t"), Map.entry("у", "u"),
            Map.entry("ф", "f"), Map.entry("х", "h"), Map.entry("ц", "c"),
            Map.entry("ч", "ch"), Map.entry("ш", "sh"), Map.entry("щ", "sch"),
            Map.entry("ъ", ""), Map.entry("ы", "y"), Map.entry("ь", ""),
            Map.entry("э", "e"), Map.entry("ю", "yu"), Map.entry("я", "ya")
    );

    @SneakyThrows
    public String getSlug(Model model) {
        List<? extends Local> locals = model.getLocalizations();
        List<Localization> localizations = locals.stream().map(Local::getLocalization).toList();
        String slug = null;
        if (localizations.contains(Localization.EN)) {
            slug = slugFromEN(locals.get(localizations.indexOf(Localization.EN)));
        }
        if (localizations.contains(Localization.RU)) {
            slug = slugFromRU(locals.get(localizations.indexOf(Localization.RU)));
        }
        if (localizations.contains(Localization.UZ)) {
            slug = slugFromUZ(locals.get(localizations.indexOf(Localization.UZ)));
        }
        if (slug == null) {
            throw new SlugException("Locals for model with id=" + model.getId() + " not found");
        }
        return slug + model.getId().toString();
    }

    private String slugify(String string) {
        return string
                .replaceAll("\r\n", "")
                .replaceAll("\n", "")
                .replaceAll("\r", "")
                .replaceAll("[^\\dA-Za-z ]", "")
                .replaceAll("\\s+", "-");
    }

    private String slugFromEN(Local local) {
        assert local.getLocalization() == Localization.EN;

        return slugify(local.getString().strip().toLowerCase());

    }

    private String slugFromRU(Local local) {
        assert local.getLocalization() == Localization.RU;

        StringBuilder result = new StringBuilder();
        for (char letter : local.getString().strip().toLowerCase().toCharArray()) {
            String tmpLetter = String.valueOf(letter);
            result.append(RUToEN.getOrDefault(tmpLetter, tmpLetter));
        }
        return slugify(result.toString());
    }

    private String slugFromUZ(Local local) {
        assert local.getLocalization() == Localization.UZ;

        return slugify(local.getString().strip().toLowerCase());
    }
}
