package ru.azor.api.core;

import com.sun.source.doctree.SerialDataTree;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto implements Serializable {
    private static final long serialVersionUID = 1810048542525448858L;

    private Long id;
    private String title;
}
