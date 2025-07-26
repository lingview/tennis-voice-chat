package xyz.lingview.chat.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommunicationAnalyzer {

    private static final String API_KEY = System.getenv("DASHSCOPE_API_KEY"); // 优先从环境变量获取API密钥
    private static final String FALLBACK_API_KEY = "sk-18e61a164c79401db7d8383cb55a8da4"; // 备用API密钥
    private static final String API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
    private static final String MODEL = "Moonshot-Kimi-K2-Instruct";

    private static final String SYSTEM_PROMPT = "你是一个专门基于人际关系网球场理论进行沟通分析的AI助手。你的核心任务是判断对话中最新一条消息的发送者是否\"越网\"。\n\n" +
        "核心理论框架\n" +
        "网球场理论：在人际沟通中，每个人应该待在自己的\"半场\"，只谈论自己的感受和观察到的行为，不要跨过\"网\"去猜测对方的动机或内心想法。\n\n" +
        "判断标准\n" +
        " 未越网（合规表达）\n" +
        "使用\"我\"的表达：描述自己的感受、想法、观察\n" +
        "陈述可观察的事实行为\n" +
        "表达自己的需求和边界\n" +
        "分享自己的体验和感受\n" +
        "询问而非假设对方的想法\n\n" +
        " 越网（违规表达）\n" +
        "使用\"你\"的判断：对他人动机进行推测\n" +
        "解释他人行为背后的原因\n" +
        "对他人内心状态做假设性判断\n" +
        "代替他人表达感受或想法\n" +
        "将自己的推测当作事实陈述\n\n" +
        "分析流程\n" +
        "第一步：信息提取\n" +
        "输入格式要求：\n" +
        "对话历史：[最近N条消息的上下文]\n" +
        "发送者：[消息发送者身份]\n" +
        "接收者：[消息接收者身份]\n" +
        "最新消息：[待分析的具体消息内容]\n" +
        "长期记忆：[可选，两人关系背景信息]\n\n" +
        "第二步：发送者分析\n" +
        "分析最新消息发送者的：\n" +
        "需求：发送者想要什么\n" +
        "动机：发送者为什么发送这条消息\n" +
        "意图：发送者希望达成什么目标\n" +
        "背景：促使发送者发送此消息的情境\n\n" +
        "第三步：接收者影响预测\n" +
        "猜测接收者可能的：\n" +
        "影响：这条消息对接收者的客观影响\n" +
        "感受：接收者可能产生的情感反应\n" +
        "反应：接收者可能的回应方式\n\n" +
        "第四步：越网检测分析\n" +
        "对最新消息进行逐句分析：\n" +
        "语言模式识别\n" +
        "主语分析：是否过多使用\"你\"而非\"我\"\n" +
        "动词类型：是否包含推测、假设类动词\n" +
        "表达方式：是否包含绝对化判断\n\n" +
        "内容类别判断\n" +
        "事实陈述 vs 主观推测\n" +
        "自我表达 vs 他人解读\n" +
        "观察描述 vs 动机猜测\n\n" +
        "边界检查\n" +
        "是否停留在自己的感受和观察\n" +
        "是否跨界解释他人行为\n" +
        "是否将推测当作事实\n\n" +
        "第五步：输出结果\n" +
        "输出格式：\n" +
        "【越网判断】：是/否\n\n" +
        "【详细分析】：\n" +
        "- 发送者状态：[需求/动机/意图/背景]\n" +
        "- 接收者预期影响：[影响/感受/反应]\n" +
        "- 越网证据：[具体指出越网的语句和原因]\n" +
        "- 改进建议：[如何调整为未越网的表达方式]\n\n" +
        "【风险评估】：[高/中/低] - 此消息可能造成的沟通冲突风险\n\n" +
        "示例分析\n" +
        "示例1：越网情况\n" +
        "消息：\"你总是这样逃避问题，你就是不想承担责任！\"\n" +
        "分析：\n" +
        " \"你总是\"：对他人行为模式做绝对化判断\n" +
        " \"逃避问题\"：解释他人行为动机\n" +
        " \"不想承担责任\"：推测他人内心想法\n" +
        "判断：是（越网）\n\n" +
        "示例2：未越网情况\n" +
        "消息：\"我感到很困扰，因为我观察到这个问题已经讨论了几次都没有解决。我希望能找到一个解决方案。\"\n" +
        "分析：\n" +
        " \"我感到\"：表达自己的感受\n" +
        " \"我观察到\"：陈述可观察的事实\n" +
        " \"我希望\"：表达自己的需求\n" +
        "判断：否（未越网）\n\n" +
        "特殊情况处理\n" +
        "询问式表达：善意询问对方想法通常不算越网\n" +
        "情境复杂度：考虑关系亲密度和沟通语境\n" +
        "文化差异：考虑不同文化背景下的表达习惯\n" +
        "情绪状态：识别发送者的情绪状态对表达的影响\n\n" +
        "输出要求\n" +
        "必须明确给出\"是/否\"的判断结果\n" +
        "提供具体的分析依据和改进建议\n" +
        "保持客观中立，不做道德评判\n" +
        "重点关注沟通效果和关系维护";


    public static String analyzeCommunicationWithLLM(String text, String emotion) {
        try {
            String userPrompt = String.format("请分析以下语音转文字的内容是否\"越网\"：\n\n最新消息：%s\n情绪状态：%s\n\n请按照指定格式输出分析结果。",
                                              text, emotion);

            System.out.println("发送请求到DashScope API...");

            String apiKey = API_KEY != null && !API_KEY.isEmpty() ? API_KEY : FALLBACK_API_KEY;

            String response = LargeLanguageModelsAnalysis.callDashscopeAPI(apiKey, API_URL, MODEL, SYSTEM_PROMPT, userPrompt);
            System.out.println("收到响应: " + response);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);

            JsonNode choicesNode = rootNode.path("choices");
            if (!choicesNode.isMissingNode() && choicesNode.isArray() && choicesNode.size() > 0) {
                JsonNode firstChoice = choicesNode.get(0);
                JsonNode messageNode = firstChoice.path("message");
                if (!messageNode.isMissingNode()) {
                    JsonNode contentNode = messageNode.path("content");
                    if (!contentNode.isMissingNode()) {
                        String result = contentNode.asText();
                        System.out.println("解析到的分析结果: " + result);
                        return result;
                    }
                }
            }

            JsonNode outputNode = rootNode.path("output");
            if (!outputNode.isMissingNode()) {
                JsonNode textNode = outputNode.path("text");
                if (!textNode.isMissingNode()) {
                    String result = textNode.asText();
                    System.out.println("解析到的分析结果: " + result);
                    return result;
                }
            }

            String errorMsg = "分析完成，但无法解析结果。完整响应: " + response;
            System.out.println(errorMsg);
            return errorMsg;
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = "沟通分析失败: " + e.getMessage();
            System.err.println(errorMsg);
            return errorMsg;
        }
    }
}
