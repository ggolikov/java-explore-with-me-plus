package ewm.category.service;

import ewm.category.dto.CategoryDto;
import ewm.category.dto.NewCategoryDto;
import ewm.category.mapper.CategoryMapper;
import ewm.category.model.Category;
import ewm.category.repository.CategoryRepository;
import ewm.common.exception.ConflictException;
import ewm.common.exception.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto create(NewCategoryDto dto) {
        if (categoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new ConflictException("Название категории должно быть уникальным");
        }
        Category category = CategoryMapper.toEntity(dto);
        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto update(Long id, CategoryDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с id=" + id + " не была найдена"));
        if (!category.getName().equalsIgnoreCase(dto.getName())
                && categoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new ConflictException("Название категории должно быть уникальным");
        }
        CategoryMapper.update(category, dto);
        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с id=" + id + " не была найдена"));
        categoryRepository.delete(category);
    }

    @Override
    public List<CategoryDto> findAll(int from, int size) {
        PageRequest page = PageRequest.of(from / size, size);
        return categoryRepository.findAll(page).stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с id=" + id + " не была найдена"));
        return CategoryMapper.toDto(category);
    }
}
