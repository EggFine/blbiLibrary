package org.blbilink.blbiLibrary.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.bukkit.plugin.Plugin;

public class TextUtil {
    private static final int CHAR_HEIGHT = 4;

    private static class CharacterDefinition {
        String[] art;

        CharacterDefinition(String[] art) {
            this.art = art;
        }
    }

    private static final Map<Character, CharacterDefinition> ASCII_ART = new HashMap<>();

    static {
        ASCII_ART.put('A', new CharacterDefinition(new String[]{
                "      ",
                " /\\   ",
                "/~~\\  ",
                "      "
        }));
        ASCII_ART.put('B', new CharacterDefinition(new String[]{
                " __   ",
                "|__)  ",
                "|__)  ",
                "      "
        }));
        ASCII_ART.put('C', new CharacterDefinition(new String[]{
                " __   ",
                "/  `  ",
                "\\__,  ",
                "      "
        }));
        ASCII_ART.put('D', new CharacterDefinition(new String[]{
                " __   ",
                "|  \\  ",
                "|__/  ",
                "      "
        }));
        ASCII_ART.put('E', new CharacterDefinition(new String[]{
                " ___  ",
                "|__   ",
                "|___  ",
                "      "
        }));
        ASCII_ART.put('F', new CharacterDefinition(new String[]{
                " ___  ",
                "|__   ",
                "|     ",
                "      "
        }));
        ASCII_ART.put('G', new CharacterDefinition(new String[]{
                " __   ",
                "/ _`  ",
                "\\__>  ",
                "      "
        }));
        ASCII_ART.put('H', new CharacterDefinition(new String[]{
                "      ",
                "|__|  ",
                "|  |  ",
                "      "
        }));
        ASCII_ART.put('I', new CharacterDefinition(new String[]{
                "   ",
                "|  ",
                "|  ",
                "   "
        }));
        ASCII_ART.put('J', new CharacterDefinition(new String[]{
                "      ",
                "   |  ",
                "\\__/  ",
                "      "
        }));
        ASCII_ART.put('K', new CharacterDefinition(new String[]{
                "      ",
                "|__/  ",
                "|  \\  ",
                "      "
        }));
        ASCII_ART.put('L', new CharacterDefinition(new String[]{
                "      ",
                "|     ",
                "|___  ",
                "      "
        }));
        ASCII_ART.put('M', new CharacterDefinition(new String[]{
                "      ",
                " |\\/| ",
                " |  | ",
                "      "
        }));
        ASCII_ART.put('N', new CharacterDefinition(new String[]{
                "      ",
                "|\\ |  ",
                "| \\|  ",
                "      "
        }));
        ASCII_ART.put('O', new CharacterDefinition(new String[]{
                " __   ",
                "/  \\  ",
                "\\__/  ",
                "      "
        }));
        ASCII_ART.put('P', new CharacterDefinition(new String[]{
                " __   ",
                "|__)  ",
                "|     ",
                "      "
        }));
        ASCII_ART.put('Q', new CharacterDefinition(new String[]{
                " __   ",
                "/  \\  ",
                "\\__X  ",
                "      "
        }));
        ASCII_ART.put('R', new CharacterDefinition(new String[]{
                " __   ",
                "|__)  ",
                "|  \\  ",
                "      "
        }));
        ASCII_ART.put('S', new CharacterDefinition(new String[]{
                " __   ",
                "/__`  ",
                ".__/  ",
                "      "
        }));
        ASCII_ART.put('T', new CharacterDefinition(new String[]{
                "___   ",
                " |    ",
                " |    ",
                "      "
        }));
        ASCII_ART.put('U', new CharacterDefinition(new String[]{
                "      ",
                "|  |  ",
                "\\__/  ",
                "      "
        }));
        ASCII_ART.put('V', new CharacterDefinition(new String[]{
                "      ",
                "\\  /  ",
                " \\/   ",
                "      "
        }));
        ASCII_ART.put('W', new CharacterDefinition(new String[]{
                "      ",
                "|  |  ",
                "|/\\|  ",
                "      "
        }));
        ASCII_ART.put('X', new CharacterDefinition(new String[]{
                "      ",
                "\\_/   ",
                "/ \\   ",
                "      "
        }));
        ASCII_ART.put('Y', new CharacterDefinition(new String[]{
                "      ",
                "\\ /   ",
                " |    ",
                "      "
        }));
        ASCII_ART.put('Z', new CharacterDefinition(new String[]{
                "__    ",
                " /    ",
                "/_    ",
                "      "
        }));
        ASCII_ART.put(' ', new CharacterDefinition(new String[]{
                "  ",
                "  ",
                "  ",
                "  "
        }));
    }

    public static String genLogo(String text) {
        StringBuilder[] lines = new StringBuilder[CHAR_HEIGHT];
        for (int i = 0; i < CHAR_HEIGHT; i++) {
            lines[i] = new StringBuilder();
        }

        for (char c : text.toUpperCase().toCharArray()) {
            CharacterDefinition charDef = ASCII_ART.getOrDefault(c, ASCII_ART.get(' '));
            for (int i = 0; i < CHAR_HEIGHT; i++) {
                lines[i].append(charDef.art[i]);
            }
        }

        StringBuilder result = new StringBuilder();
        for (StringBuilder line : lines) {
            result.append(line.toString().replaceFirst("\\s+$", "")).append("\n");
        }
        return result.toString();
    }

    public static String getLogo(@Nullable String str, String logoText, @Nullable String subTitle, Plugin plugin, @Nullable List<String> mainAuthor, @Nullable List<String> subAuthor) {
        String logo = "\n\n\n\n\n"
                + genLogo(logoText)
                + "\n                                                         " + plugin.getDescription().getVersion() + " " + str;
        if (subTitle != null) {
            logo = logo + "\n" + subTitle;
        }
        if (mainAuthor != null) {
            logo = logo + "\n主要开发人员(Main Author): ";
            for (String author : mainAuthor) {
                logo = logo + author + " ";
            }
        }
        if (subAuthor != null) {
            logo = logo + "\n次要开发人员(Sub Author): ";
            for (String author : subAuthor) {
                logo = logo + author + " ";
            }
        }
        logo = logo + "\n\n\n\n\n";
        return logo;
    }
}