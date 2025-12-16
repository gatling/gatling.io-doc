

---
title: Gatling AI Assistant for Cursor
seotitle: Gatling AI Assistant Cursor Extension
menutitle: AI Assistant for Cursor
description: AI-powered assistant for Gatling performance testing in Cursor. Get intelligent help with creating, optimizing, and understanding Gatling simulations.
lead: AI-powered assistant for Gatling performance testing. Get intelligent help with creating, optimizing, and understanding Gatling simulations directly in Cursor.
---
## Overview
The Gatling AI Assistant for Cursor integrates AI-powered development tools directly into your editor. Write, optimize, and understand Gatling simulations in JavaScript, TypeScript, Java, Scala, and Kotlin with intelligent assistance.

## What it does

- **Create simulations**: Generate new test scenarios from scratch with guided prompts
- **Explain code**: Select any Gatling code to get contextual explanations
- **Refine selection**: Improve existing code with AI-powered suggestions
- **Ask questions**: Get instant answers about Gatling concepts, patterns, and best practices
- **Convert LoadRunner scripts**: Automatically migrate LoadRunner C scripts to Gatling

{{< alert warning >}}
**AI-Generated Code Notice**: This extension uses AI language models to generate code. AI models can make mistakes or produce incomplete solutions. Always verify generated code through testing, peer review, and performance validation before using in production.
{{< /alert >}}

## Installation and setup

### Install the extension

**From Cursor Marketplace:**
1. Open Cursor
2. Go to Extensions
3. Search for "Gatling AI Assistant"
4. Click Install


### Configure an API key

The extension requires an API key from one of these providers:

1. Open the Command Palette (`Ctrl+Shift+P` / `Cmd+Shift+P`)
2. Run `Gatling: Set API Key`
3. Choose your provider:
   - **OpenAI** (recommended): [platform.openai.com](https://platform.openai.com)
   - **Anthropic Claude**: [console.anthropic.com](https://console.anthropic.com)
   - **Azure OpenAI**: [portal.azure.com](https://portal.azure.com)
4. Paste your API key when prompted

API keys are stored securely in Cursor's secrets storage and never sent to Gatling servers.

## Features and Usage

The Gatling AI Assistant for Cursor has an identical scope as the VS Code extension. Please refer to the [Gatling AI Assistant for VS Code](/integrations/ide-tools/vscode/) documentation for detailed information on features and usage instructions.
