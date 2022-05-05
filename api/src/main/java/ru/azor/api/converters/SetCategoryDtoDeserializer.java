package ru.azor.api.converters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.*;
import ru.azor.api.core.CategoryDto;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SetCategoryDtoDeserializer extends JsonDeserializer<Set<CategoryDto>> {
    @Override
    public Set<CategoryDto> deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String json = jsonParser.getValueAsString();
        Set<String> categoryTitles = Set.copyOf(Arrays.asList(json.split(" ")));
        Set<CategoryDto> categoriesDto = new HashSet<>();
        for (String categoryTitle : categoryTitles) {
            categoriesDto.add(new CategoryDto(null, categoryTitle.trim()));
        }
        return categoriesDto;
    }
}
