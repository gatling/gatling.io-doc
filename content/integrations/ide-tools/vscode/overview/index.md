---
title: Overview and Getting Started
seotitle: Gatling AI Assistant Visual Studio Code Extension
menutitle: Get Started
description: AI-powered assistant for Gatling performance testing. Get intelligent help with creating, optimizing, and understanding Gatling simulations.
lead: AI-powered assistant for Gatling performance testing in your VS Code IDE
---

The Gatling AI Assistant for VS Code integrates AI-powered development tools directly into your editor. Write, optimize, and understand Gatling simulations in JavaScript, TypeScript, Java, Scala, and Kotlin with intelligent assistance.

## What it does

- **Create simulations**: Generate new test scenarios from scratch with guided prompts
- **Explain code**: Select any Gatling code to get contextual explanations
- **Refine selection**: Improve existing code with AI-powered suggestions
- **Ask questions**: Get instant answers about Gatling concepts, patterns, and best practices
- **Migrate LoadRunner scripts**: Migrate LoadRunner C scripts to Gatling Java simulations using an AI agent workflow
- **Persistent chat history**: Conversations are automatically saved and restored across sessions

{{< alert warning >}}
**AI-Generated Code Notice**: This extension uses AI language models to generate code. AI models can make mistakes or produce incomplete solutions. Always verify generated code through testing, peer review, and performance validation before using in production.
{{< /alert >}}

## Installation and setup

### Install the extension

**From VS Code Marketplace:**
1. Open VS Code
2. Go to Extensions (`Ctrl+Shift+X` / `Cmd+Shift+X`)
3. Search for "Gatling AI Assistant"
4. Click Install

**From command line:**
```bash
code --install-extension GatlingCorp.gatling-ai-assistant
```

For VSCodium and compatible editors (using Open VSX):
```bash
codium --install-extension GatlingCorp.gatling-ai-assistant
```

### Configure an API key

The extension requires an API key from one of these providers:

1. Open the Command Palette (`Ctrl+Shift+P` / `Cmd+Shift+P`)
2. Run `Gatling: Set API Key`
3. Choose your provider:
   - **OpenAI** (recommended): [platform.openai.com](https://platform.openai.com)
   - **Anthropic Claude**: [console.anthropic.com](https://console.anthropic.com)
   - **Azure OpenAI**: [portal.azure.com](https://portal.azure.com)
4. Paste your API key when prompted

API keys are stored securely in VS Code's secrets storage and never sent to Gatling servers.

## Getting started

1. **Open the Gatling panel**: Click the Gatling icon in the Activity Bar (left sidebar)
2. **Start chatting**: Ask questions about Gatling or request help with your project
3. **Explain code**: Select code and choose "Explain Code" from the context menu
4. **Create a simulation**: See [Create a Simulation]({{< ref "/integrations/ide-tools/vscode/create-simulation" >}}) for step-by-step guidance

## Features

### [Create a Simulation]({{< ref "/integrations/ide-tools/vscode/create-simulation" >}})
Generate new Gatling test scenarios with AI guidance. The assistant helps structure simulations effectively, choose appropriate protocols, and set up realistic user behavior patterns.

### [Explain Code]({{< ref "/integrations/ide-tools/vscode/explain-code" >}})
Select any portion of your Gatling code and receive contextual explanations. Understand complex patterns, configuration options, and performance implications.

### [Refine Selection]({{< ref "/integrations/ide-tools/vscode/refine-selection" >}})
Improve existing code with AI-powered refinements. Select code and specify your goal—optimize performance, add error handling, improve clarity, or apply best practices. Review changes with side-by-side diff before accepting.

### [Contextual Chat]({{< ref "/integrations/ide-tools/vscode/contextual-chat" >}})
Ask the AI assistant questions about Gatling concepts, best practices, and implementation details. Get help with optimization and troubleshooting.

### [LoadRunner Script Migration]({{< ref "/integrations/ide-tools/vscode/loadrunner-converter" >}})
Migrate existing LoadRunner C scripts into Gatling Java simulations using an AI agent workflow. The agent parses, analyzes, transforms, and generates code with step-by-step progress visibility and diff-based output for review. Right-click `.c` files in the Explorer to migrate single scripts, or use the Command Palette for batch migration.

## Configuration

### Settings

Access settings via **File → Preferences → Settings → Extensions → Gatling AI Assistant**:

| Setting | Default | Description |
|---------|---------|-------------|
| `gatling.context.enableRedaction` | `true` | Automatically redact API keys, passwords, and secrets from prompts |
| `gatling.context.sendConfigContent` | `true` | Include Gatling configuration file contents in AI requests |
| `gatling.simulationDirectory.enableAutoDetection` | `true` | Auto-detect and share project structure information |

### API key management

**Set or update:** Run `Gatling: Set API Key` in the Command Palette

**Remove:** Run `Gatling: Remove API Key` in the Command Palette

## Privacy and security

- **No intermediary**: Direct communication with your chosen AI provider (OpenAI, Anthropic, or Azure)
- **Local storage**: API keys encrypted and stored securely in VS Code
- **No Gatling logging**: Your code and conversations are never logged by Gatling
- **Automatic redaction**: API keys, passwords, tokens, and credentials are automatically stripped before sending code to AI
- **Workspace isolation**: Chat history is isolated per workspace

**What's shared with AI providers:**
- Selected code (with automatic redaction of sensitive data)
- Current file name and programming language
- Current file content (when relevant to the query)
- Workspace and folder names
- Project dependencies from `package.json`, `pom.xml`, `build.gradle`, or `build.sbt`
- Nearby Gatling simulation files (limited selection)
- Open file names (limited to relevant files)
- Gatling configuration file contents (unless disabled via settings)

## Supported providers

| Provider | Model | Best For |
|----------|-------|----------|
| **OpenAI** | gpt-4o-2024-11-20 | General use, fast responses, comprehensive Gatling knowledge |
| **Anthropic** | claude-3-5-sonnet-20241022 | Complex reasoning, detailed analysis, nuanced code review |
| **Azure OpenAI** | gpt-4o-2024-11-20 | Enterprise deployments, data residency, compliance requirements |

## Requirements

- **VS Code**: 1.85.0 or higher (including Cursor and compatible forks)
- **OS**: Windows, macOS, or Linux
- **Network**: Internet connection required for AI provider communication

## Support

- **Report issues or request features**: [ProductBoard](https://portal.productboard.com/gatling/1-gatling-roadmap/c/116-ide-based-ai-assistants?utm_medium=vscode&utm_source=feedback)

## License

Licensed under the [Gatling Enterprise Component License]({{< ref "/project/licenses/enterprise-component/" >}}).