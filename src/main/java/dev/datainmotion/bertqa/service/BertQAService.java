package dev.datainmotion.bertqa.service;

import ai.djl.Application;
import ai.djl.ModelException;
import ai.djl.engine.Engine;
import ai.djl.inference.Predictor;
import ai.djl.modality.nlp.qa.QAInput;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import ai.djl.Device;
import ai.djl.MalformedModelException;
import ai.djl.modality.Classifications;
import ai.djl.repository.zoo.ModelNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * See:   https://github.com/deepjavalibrary/djl/blob/master/examples/docs/BERT_question_and_answer.md
 * See:   https://github.com/deepjavalibrary/djl/blob/master/jupyter/BERTQA.ipynb
 *
 */
public class BertQAService {

    private static final Logger logger = LoggerFactory.getLogger(BertQAService.class);

    public static void main(String[] args) throws IOException, TranslateException, ModelException {
        String answer = BertQAService.predict();
        logger.info("Answer: {}", answer);

        Classifications classifications = BertQAService.predict2();
        logger.info(classifications.toString());
        Classifications.Classification classPositive = classifications.get("Positive");
        Classifications.Classification classNegative = classifications.get("Negative");

        logger.info("Pos: {} = {}",classPositive.getClassName(),classPositive.getProbability());
        logger.info("Neg: {} = {}",classNegative.getClassName(),classNegative.getProbability());

        if ( classPositive.getProbability() > classNegative.getProbability()) {
            logger.info("Sentiment is positive");
        }
        else if (classNegative.getProbability() > classPositive.getProbability())
        {
            logger.info("Sentiment is negative");
        }
        else {
            logger.info("Sentiment is neutral");
        }
    }

    public static Classifications predict2()
            throws MalformedModelException, ModelNotFoundException, IOException,
            TranslateException {
        String input = "I like Apache Pulsar. Apache Pulsar is the best unified messaging and streaming platform!";
        logger.info("input Sentence: {}", input);

        Criteria<String, Classifications> criteria =
                Criteria.builder()
                        .optApplication(Application.NLP.SENTIMENT_ANALYSIS)
                        .setTypes(String.class, Classifications.class)
                        // This model was traced on CPU and can only run on CPU
                        .optDevice(Device.cpu())
                        .optProgress(new ProgressBar())
                        .build();

        try (ZooModel<String, Classifications> model = criteria.loadModel();
             Predictor<String, Classifications> predictor = model.newPredictor()) {
            return predictor.predict(input);
        }
    }

    public static String predict() throws IOException, TranslateException, ModelException {
        //        String question = "How is the weather";
        //        String paragraph = "The weather is nice, it is beautiful day";
        String question = "When did BBC Japan start broadcasting?";
        String paragraph =
                "BBC Japan was a general entertainment Channel. "
                        + "Which operated between December 2004 and April 2006. "
                        + "It ceased operations after its Japanese distributor folded.";

        QAInput input = new QAInput(question, paragraph);
        logger.info("Paragraph: {}", input.getParagraph());
        logger.info("Question: {}", input.getQuestion());

        Criteria<QAInput, String> criteria =
                Criteria.builder()
                        .optApplication(Application.NLP.QUESTION_ANSWER)
                        .setTypes(QAInput.class, String.class)
                        .optFilter("backbone", "bert")
                        .optEngine(Engine.getDefaultEngineName())
                        .optProgress(new ProgressBar())
                        .build();

        try (ZooModel<QAInput, String> model = criteria.loadModel()) {
            try (Predictor<QAInput, String> predictor = model.newPredictor()) {
                return predictor.predict(input);
            }
        }
    }
}
