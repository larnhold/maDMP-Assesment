package org.arnhold.evaluation.shacl;

import org.arnhold.semantic.SemanticService;
import org.arnhold.semantic.shacl.ShaclValidationService;
import org.example.dcsojson.DcsoJsonTransformer;
import org.example.dcsojson.TransformationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/legacy")
public class OldShaclController {

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    SemanticService semanticService;

    @Autowired
    DcsoJsonTransformer dcsoJsonTransformer;

    @Autowired
    ShaclValidationService shaclValidationService;

    @Autowired
    ResourcePatternResolver resourcePatternResolver;


    @GetMapping("/")
    public List<ShaclValidationResult> verify() throws IOException, TransformationException {
        List<File> shapes = Arrays.stream(resourcePatternResolver.getResources("classpath:/original-evaluation/shacl_constraints/*.ttl")).map(element -> {
            try {
                return element.getFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();

        File maDMP = resourceLoader.getResource("classpath:/maDMPs/examples/ex1-header-fundedProject.json").getFile();
        var maDMPModel = dcsoJsonTransformer.convertPlainToModel(maDMP);

        return shapes.stream().map(x -> {
            var model = semanticService.loadModelFromFile(x);
            var validation = shaclValidationService.validateShape(maDMPModel ,model);

            return new ShaclValidationResult(x.getName(), validation.conforms());
        }).toList();
    }
}
