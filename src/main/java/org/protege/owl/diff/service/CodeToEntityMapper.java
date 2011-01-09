package org.protege.owl.diff.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.protege.owl.diff.align.OwlDiffMap;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;

public class CodeToEntityMapper {
	public static final Logger LOGGER = Logger.getLogger(CodeToEntityMapper.class);
	
	public static final String CODE_ANNOTATION_PROPERTY = "code.annotation.property";
	
	private OwlDiffMap                         diffMap;
	private OWLAnnotationProperty              codeProperty;
	private Map<String, Collection<OWLEntity>> targetCodeToEntityMap;
	
	public static CodeToEntityMapper generateCodeToEntityMap(OwlDiffMap diffMap, Properties parameters) {
		CodeToEntityMapper mapper = diffMap.getService(CodeToEntityMapper.class);
		if (mapper == null) {
			mapper = new CodeToEntityMapper(diffMap, parameters);
			diffMap.addService(mapper);
		}
		return mapper;
	}

	
	private CodeToEntityMapper(OwlDiffMap diffMap, Properties parameters) {
		this.diffMap = diffMap;
		OWLOntology ontology = diffMap.getSourceOntology();
		String codeName = (String) parameters.get(CODE_ANNOTATION_PROPERTY);
        IRI codeIri = IRI.create((String) codeName);
        codeProperty = diffMap.getOWLDataFactory().getOWLAnnotationProperty(codeIri);
        if (!diffMap.getSourceOntology().containsAnnotationPropertyInSignature(codeIri)) {
        	LOGGER.warn("Source ontology does not have selected code annotation " + codeName);
        }
        else if (!diffMap.getTargetOntology().containsAnnotationPropertyInSignature(codeIri)) {
        	LOGGER.warn("Target ontology does not have selected code annotation " + codeName);
        }

	}
	
	public boolean codeNotPresent() {
		return !diffMap.getSourceOntology().containsAnnotationPropertyInSignature(codeProperty.getIRI()) 
			|| !diffMap.getTargetOntology().containsAnnotationPropertyInSignature(codeProperty.getIRI());
	}
	
	public OWLAnnotationProperty getCodeProperty() {
		return codeProperty;
	}
	
    public String getCode(OWLOntology ontology, OWLEntity entity) {
        for (OWLAnnotation annotation : entity.getAnnotations(ontology)) {
            if (!annotation.getProperty().equals(codeProperty)) {
                continue;
            }
            OWLAnnotationValue value = annotation.getValue();
            if (value instanceof OWLLiteral) {
                return ((OWLLiteral) value).getLiteral();
            }
        }
        return null;
    }
    
    public Collection<OWLEntity> getTargetEntities(String code) {
    	Collection<OWLEntity> targetEntities = getTargetCodeToEntityMap().get(code);
    	if (targetEntities == null) {
    		return Collections.emptySet();
    	}
    	else {
    		return Collections.unmodifiableCollection(targetEntities);
    	}
    }
    
    private Map<String, Collection<OWLEntity>> getTargetCodeToEntityMap() {
    	if (targetCodeToEntityMap == null) {
    		targetCodeToEntityMap = generateCodeToEntityMap(diffMap.getTargetOntology());
    	}
    	return Collections.unmodifiableMap(targetCodeToEntityMap);
    }


    private Map<String, Collection<OWLEntity>> generateCodeToEntityMap(OWLOntology ontology) {
    	Map<String, Collection<OWLEntity>> codeToEntityMap = new HashMap<String, Collection<OWLEntity>>();
        for (OWLEntity entity : ontology.getSignature()) {
            String code = getCode(ontology, entity);
            if (code != null) {
                Collection<OWLEntity> entities = codeToEntityMap.get(code);
                if (entities == null) {
                    entities = new ArrayList<OWLEntity>();
                    codeToEntityMap.put(code, entities);
                }
                entities.add(entity);
            }
        }
        return Collections.unmodifiableMap(codeToEntityMap);
    }
    
    public class UnmodifiableMap<X, Y> implements Map<X, Collection<Y>> {

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean containsKey(Object key) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean containsValue(Object value) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Collection<Y> get(Object key) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Collection<Y> put(X key, Collection<Y> value) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Collection<Y> remove(Object key) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void putAll(Map<? extends X, ? extends Collection<Y>> m) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Set<X> keySet() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Collection<Collection<Y>> values() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Set<java.util.Map.Entry<X, Collection<Y>>> entrySet() {
			// TODO Auto-generated method stub
			return null;
		}
    	
    }
}
