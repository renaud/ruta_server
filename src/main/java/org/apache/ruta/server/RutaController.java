package org.apache.ruta.server;

import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.resource.ResourceInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;

@Controller
@EnableAutoConfiguration
@ComponentScan
public class RutaController {

	private RutaEngine rutaEngine;

	@Autowired
	public void setRutaEngine(RutaEngine rutaEngine) {
		this.rutaEngine = rutaEngine;
	}

	@RequestMapping("/annotate")
	@ResponseBody
	Map<String, Object> recommend(@RequestParam String text)
			throws AnalysisEngineProcessException,
			ResourceInitializationException, CASException {

		Map<String, Object> response = Maps.newHashMap();
		response.put("text", text);
		response.put("annotations", rutaEngine.process(text));
		return response;
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(RutaController.class, args);
	}
}