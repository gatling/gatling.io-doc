---
title: Gatling AI Assistant for VS Code
seotitle: Gatling AI Assistant Visual Studio Code Extension
menutitle: AI Assistant for VS Code
description: AI-powered assistant for Gatling performance testing in VS Code. Get intelligent help with creating, optimizing, and understanding Gatling simulations.
lead: AI-powered assistant for Gatling performance testing. Get intelligent help with creating, optimizing, and understanding Gatling simulations directly in VS Code.
---
The Gatling AI Assistant brings AI-powered performance testing directly to VS Code. Key benefits include:

- **Intelligent guidance**: Create and optimize Gatling simulations with AI assistance
- **Multi-language support**: Work with JavaScript, TypeScript, Java, Scala, and Kotlin
- **Contextual help**: Get relevant assistance based on your specific Gatling project
- **Streamlined workflow**: Build effective performance tests faster than ever


{{< alert info >}}
**AI-Generated Content Notice**: This extension uses AI language models to assist with Gatling code generation and analysis. While these models are powerful tools, they can make mistakes, generate incorrect code, or provide incomplete solutions.

**Always verify AI-generated code** through:
- Thorough testing in your environment
- Peer code review
- Performance validation
- Security assessment

**You are responsible** for ensuring that any AI-generated code meets your quality standards, security requirements, and performance expectations before using it in production environments.
{{< /alert >}}

## Quick start

Get up and running with the Gatling AI Assistant in minutes:

### 1. Installation

Install the extension from the VS Code Marketplace:

- Open VS Code
- Go to the Extensions view (`Ctrl+Shift+X` / `Cmd+Shift+X`)
- Search for "Gatling AI Assistant"
- Click "Install"

Alternatively, install via command line:
```bash
code --install-extension GatlingCorp.gatling-ai-assistant
```

### 2. Access the Gatling sidebar

- Look for the Gatling icon in the Activity Bar (left sidebar)
- Click the icon to open the Gatling AI Assistant panel

### 3. Configure your API key

Set up your chosen LLM provider:

1. Open the Command Palette (`Ctrl+Shift+P` / `Cmd+Shift+P`)
2. Type "Gatling: Set API Key"
3. Select your provider (OpenAI, Anthropic, or Azure OpenAI)
4. Enter your API key when prompted

### 4. Start using the AI assistant

- Begin chatting with the AI assistant in the sidebar
- Select code and use "Explain Code" from the context menu
- Ask questions about Gatling concepts and best practices

## Features

The Gatling AI Assistant provides comprehensive support for your performance testing workflow with the following key features:

### AI chat assistant

Get instant, intelligent assistance with your Gatling development:

- **Real-time Q&A**: Ask questions about Gatling concepts, best practices, and implementation details
- **Code optimization**: Receive suggestions for improving performance and efficiency of your simulations
- **Best practices guidance**: Learn recommended approaches for common testing scenarios
- **Troubleshooting help**: Get assistance with debugging and resolving issues in your test scripts

### Create simulations

Generate new Gatling test scenarios with AI guidance across multiple programming languages:

- **JavaScript**: Create modern, ES6+ Gatling simulations
- **TypeScript**: Build type-safe performance tests with full TypeScript support
- **Java**: Generate robust Java-based Gatling simulations
- **Scala**: Leverage Scala's functional programming features for complex test scenarios
- **Kotlin**: Create concise and expressive Kotlin-based performance tests

The AI assistant helps you:
- Structure your simulations effectively
- Choose appropriate protocols and configurations
- Implement realistic user behavior patterns
- Set up proper load injection strategies

### Explain code

Select any portion of your Gatling code and receive contextual explanations:

- **Contextual analysis**: Get explanations tailored to your specific code selection
- **Concept clarification**: Understand complex Gatling concepts and patterns
- **Parameter explanations**: Learn about configuration options and their impacts
- **Performance implications**: Understand how code changes affect test behavior

### Enterprise-ready

Built with enterprise security and compliance in mind:

- **Multiple LLM providers**: Choose from OpenAI, Anthropic Claude, or Azure OpenAI
- **Secure credential storage**: API keys stored using VS Code's secure secrets API
- **Data governance**: Control what information is shared with AI providers
- **Audit trail**: Track AI interactions for compliance requirements

## Supported LLM providers

The extension supports multiple AI providers to meet different organizational needs:

### OpenAI (Recommended)

- **Model**: GPT-4o
- **Best for**: General performance, fast responses, and comprehensive Gatling knowledge
- **Setup**: Requires OpenAI API key
- **Pricing**: Pay-per-use based on OpenAI's pricing model

### Anthropic Claude

- **Model**: Claude 3.5 Sonnet
- **Best for**: Complex reasoning, detailed explanations, and nuanced code analysis
- **Setup**: Requires Anthropic API key
- **Pricing**: Pay-per-use based on Anthropic's pricing model

### Azure OpenAI (Experimental)

- **Best for**: Enterprise compliance, data governance, and regulatory requirements
- **Features**: 
  - Data residency controls
  - Enterprise-grade security
  - Compliance with organizational policies
- **Setup**: Requires Azure OpenAI service deployment and endpoint configuration
- **Note**: Currently in experimental phase

## Configuration

### API key management

API keys are stored securely using VS Code's built-in secrets storage:

- Keys are encrypted and stored locally
- No keys are transmitted to Gatling servers
- Keys can be updated or removed at any time

To manage your API keys:
1. Command Palette → "Gatling: Set API Key" (to set/update)
2. Command Palette → "Gatling: Remove API Key" (to remove)

### Extension settings

Configure the extension behavior through VS Code settings:

- **`gatling.context.enableRedaction`** (default: `true`): Automatically redact sensitive information from prompts before sending to AI providers
- **`gatling.context.sendConfigContent`** (default: `true`): Control whether Gatling configuration file contents are included in AI requests (when disabled, only filenames are shared)
- **`gatling.simulationDirectory.enableAutoDetection`** (default: `true`): Enable automatic project structure detection and sharing

Access these settings via:
- File → Preferences → Settings → Extensions → Gatling AI Assistant
- Or search for "gatling" in the settings search bar

### Context sharing controls

The extension automatically shares context with AI providers to provide relevant assistance. You can control some aspects of what information is shared:

**Currently Available Controls:**
- **Configuration content**: Control whether Gatling configuration file contents are included in prompts (vs. filenames only) using the `gatling.context.sendConfigContent` setting
- **Project structure detection**: Enable/disable automatic project structure detection with `gatling.simulationDirectory.enableAutoDetection`
- **Automatic redaction**: Built-in redaction of sensitive data is enabled by default via `gatling.context.enableRedaction`

**What's Always Shared:**
- Current file name and programming language
- Selected code (with automatic redaction applied)
- Current file content snippet (first 50 lines, up to 2000 characters)
- Workspace name and folder information
- Project dependencies (Gatling-related and relevant ones)
- Nearby Gatling simulation files (up to 5 files)
- Open file names (up to 10 files)
- Error messages and health check information (when applicable)

## Privacy & security

### Data handling

- **Local storage**: API keys stored securely using VS Code's secrets API
- **No intermediary**: Direct communication with your chosen LLM provider
- **No logging**: Gatling does not log or store your code or conversations
- **Provider choice**: You control which AI service processes your data

### Automatic data redaction

The extension automatically identifies and removes sensitive information before sending code to AI providers:

- **API keys and tokens**: Automatically detected and redacted using regex patterns
- **Passwords**: Removed from code before sending to AI
- **AWS credentials**: Specific patterns for `AWS_ACCESS_KEY` and `AWS_SECRET_KEY`
- **Bearer tokens**: JWT and authorization tokens are automatically redacted
- **Generic secrets**: Long secret values (16+ characters) are automatically detected and removed

{{< alert info >}}
Automatic redaction is controlled by the `gatling.context.enableRedaction` setting, which is enabled by default for your security.
{{< /alert >}}

### Available privacy controls

Current privacy features include:

- **Workspace isolation**: Chat history is automatically isolated per workspace
- **Configuration content control**: Use `gatling.context.sendConfigContent` to exclude configuration file contents from AI requests
- **Secure credential storage**: API keys stored using VS Code's secure secrets API
- **Automatic redaction**: Built-in protection against common secrets and credentials (enabled by default)

## Requirements

### System requirements

- **VS Code Version**: 1.95.0 or higher
- **Operating System**: Windows, macOS, or Linux
- **Internet Connection**: Required for AI provider communication

### API requirements

You'll need an API key from one of the supported providers:

- **OpenAI**: Sign up at [platform.openai.com](https://platform.openai.com)
- **Anthropic**: Get access at [console.anthropic.com](https://console.anthropic.com)
- **Azure OpenAI**: Set up through Azure portal with proper deployments

## Support & feedback

We value your input and are committed to improving the Gatling AI Assistant:

### Share feedback

- **ProductBoard**: [Report bugs, request features, and share ideas](https://portal.productboard.com/gatling/1-gatling-roadmap/c/116-ide-based-ai-assistants?utm_medium=vscode&utm_source=feedback)
- **Feature Requests**: Vote on and suggest new capabilities
- **Bug Reports**: Help us identify and fix issues
- **User Experience**: Share your workflow insights and suggestions

## License

This project is licensed under the Gatling Enterprise Component License. See the [LICENSE](LICENSE) file for complete details and terms of use.

The extension is built to integrate with commercial AI services, and usage may be subject to the terms and pricing of your chosen AI provider.
