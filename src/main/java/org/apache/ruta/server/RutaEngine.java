package org.apache.ruta.server;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineFromPath;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;

@Component
@EnableAutoConfiguration
public class RutaEngine {

	private Logger LOG = getLogger(RutaEngine.class);

	private Set<String> skipAnnots = newHashSet("RutaBasic", "SPACE",
			"DocumentAnnotation", "SPECIAL", "SW", "NUM");
	private AnalysisEngine engine;

	@Autowired
	public RutaEngine(@Value("${ruta.script}") String scriptName,
			@Value("${ruta.project.root}") String projectRoot)
			throws InvalidXMLException, ResourceInitializationException,
			IOException {

		LOG.info("Using script '{}' in project '{}'", scriptName, projectRoot);

		checkNotNull(scriptName, "no scriptName provided");
		checkNotNull(projectRoot, "no projectRoot provided");

		String scriptPath = projectRoot + "/" + scriptName;
		checkArgument(new File(scriptPath).exists(), "script at path '"
				+ scriptPath + "' does not exists");
		engine = createEngineFromPath(scriptPath);
	}

	public List<AnnotationWrapper> process(String text)
			throws ResourceInitializationException,
			AnalysisEngineProcessException, CASException {

		// process text with Ruta
		final CAS cas = engine.newCAS();
		cas.setDocumentText(text);
		engine.process(cas);

		// add annotations to response, skip some
		ArrayList<AnnotationWrapper> annotations = newArrayList();
		FSIterator<AnnotationFS> it = cas.getAnnotationIndex().iterator();
		while (it.hasNext()) {
			AnnotationFS a = it.next();
			String aName = a.getType().getShortName();
			if (!skipAnnots.contains(aName)) {
				annotations.add(new AnnotationWrapper(aName, a.getBegin(), a
						.getEnd(), a.getCoveredText()));
			}
		}
		return annotations;
	}

	public static class AnnotationWrapper {

		public int start, end;
		public String type;
		public String text;
		public Map<String, String> attributes;

		public AnnotationWrapper(String type, int start, int end, String text) {
			this.type = type;
			this.start = start;
			this.end = end;
			this.text = text;
		}

	}
}
