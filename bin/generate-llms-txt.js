#!/usr/bin/env node

const fs = require('fs');
const path = require('path');

// Configuration
const CONFIG = {
  contentDir: 'content',
  outputDir: 'public',
  baseUrl: 'https://docs.gatling.io',
  siteTitle: 'Gatling Documentation',
  siteDescription: 'Documentation for the Gatling Community and Enterprise Edition load testing tools.'
};

/**
 * Parse YAML frontmatter from markdown content
 */
function parseFrontmatter(content) {
  const frontmatterRegex = /^---\n([\s\S]*?)\n---/;
  const match = content.match(frontmatterRegex);

  if (!match) {
    return { frontmatter: {}, content };
  }

  const yamlContent = match[1];
  const frontmatter = {};

  // Simple YAML parsing for flat key-value pairs and arrays
  const lines = yamlContent.split('\n');
  let currentKey = null;
  let inArray = false;

  for (const line of lines) {
    // Check for array item
    if (inArray && line.match(/^\s+-\s+/)) {
      const value = line.replace(/^\s+-\s+/, '').trim();
      if (!frontmatter[currentKey]) {
        frontmatter[currentKey] = [];
      }
      frontmatter[currentKey].push(value);
      continue;
    }

    // Check for key-value pair
    const kvMatch = line.match(/^(\w+):\s*(.*)$/);
    if (kvMatch) {
      currentKey = kvMatch[1];
      const value = kvMatch[2].trim();

      if (value === '') {
        // Might be start of array
        inArray = true;
        frontmatter[currentKey] = [];
      } else {
        inArray = false;
        // Remove quotes if present
        frontmatter[currentKey] = value.replace(/^["']|["']$/g, '');
      }
    }
  }

  const markdownContent = content.slice(match[0].length).trim();
  return { frontmatter, content: markdownContent };
}

/**
 * Get all markdown files recursively
 */
function getMarkdownFiles(dir, files = [], isRoot = true) {
  const entries = fs.readdirSync(dir, { withFileTypes: true });

  for (const entry of entries) {
    const fullPath = path.join(dir, entry.name);
    if (entry.isDirectory()) {
      getMarkdownFiles(fullPath, files, false);
    } else if (entry.name.endsWith('.md') && !entry.name.startsWith('_')) {
      // Skip non-index files in root content directory (like search.md)
      if (isRoot) {
        continue;
      }
      files.push(fullPath);
    }
  }

  return files;
}

/**
 * Convert file path to URL
 */
function filePathToUrl(filePath) {
  const relativePath = path.relative(CONFIG.contentDir, filePath);
  let urlPath = relativePath
    .replace(/\\/g, '/')
    .replace(/\/index\.md$/, '')
    .replace(/\/_index\.md$/, '')
    .replace(/\.md$/, '');

  if (urlPath === '' || urlPath === '_index') {
    return CONFIG.baseUrl;
  }

  return `${CONFIG.baseUrl}/${urlPath}`;
}

/**
 * Get section ordering from root _index.md
 */
function getSectionOrder() {
  const indexPath = path.join(CONFIG.contentDir, '_index.md');
  if (!fs.existsSync(indexPath)) {
    return [];
  }

  const content = fs.readFileSync(indexPath, 'utf8');
  const { frontmatter } = parseFrontmatter(content);
  return frontmatter.ordering || [];
}

/**
 * Get section title from its _index.md
 */
function getSectionTitle(section) {
  const indexPath = path.join(CONFIG.contentDir, section, '_index.md');
  if (!fs.existsSync(indexPath)) {
    return formatSectionName(section);
  }

  const content = fs.readFileSync(indexPath, 'utf8');
  const { frontmatter } = parseFrontmatter(content);
  // Use title, menutitle, or fall back to formatted section name if empty
  const title = frontmatter.title || frontmatter.menutitle;
  if (typeof title === 'string' && title.trim()) {
    return title;
  }
  return formatSectionName(section);
}

/**
 * Format section name for display
 */
function formatSectionName(section) {
  return section
    .split('-')
    .map(word => word.charAt(0).toUpperCase() + word.slice(1))
    .join(' ');
}

/**
 * Parse all content files and organize by section
 */
function parseContentFiles(files) {
  const sections = {};
  const allPages = [];

  for (const file of files) {
    const content = fs.readFileSync(file, 'utf8');
    const { frontmatter, content: markdownContent } = parseFrontmatter(content);

    // Skip files without titles or draft files
    if (!frontmatter.title || frontmatter.draft === 'true') {
      continue;
    }

    const relativePath = path.relative(CONFIG.contentDir, file);
    const pathParts = relativePath.split(path.sep);
    const section = pathParts[0] || 'other';

    const url = filePathToUrl(file);

    const pageData = {
      title: frontmatter.title,
      description: frontmatter.description || '',
      url: url,
      mdUrl: url + '.md',
      weight: parseInt(frontmatter.weight, 10) || 999,
      section: section,
      content: markdownContent,
      filePath: file,
      isIndex: file.endsWith('_index.md') || (file.endsWith('index.md') && pathParts.length <= 2)
    };

    if (!sections[section]) {
      sections[section] = [];
    }
    sections[section].push(pageData);
    allPages.push(pageData);
  }

  // Sort pages within each section by weight, then by title
  for (const section of Object.keys(sections)) {
    sections[section].sort((a, b) => {
      if (a.weight !== b.weight) {
        return a.weight - b.weight;
      }
      return a.title.localeCompare(b.title);
    });
  }

  return { sections, allPages };
}

/**
 * Generate llms.txt (index file)
 */
function generateLLMSTxt(sections, sectionOrder) {
  let output = `# ${CONFIG.siteTitle}\n\n`;
  output += `> ${CONFIG.siteDescription}\n\n`;

  // Add sections in defined order
  for (const sectionKey of sectionOrder) {
    if (sections[sectionKey] && sections[sectionKey].length > 0) {
      const sectionTitle = getSectionTitle(sectionKey);
      output += `## ${sectionTitle}\n\n`;

      for (const page of sections[sectionKey]) {
        const desc = page.description ? `: ${page.description}` : '';
        output += `- [${page.title}](${page.mdUrl})${desc}\n`;
      }

      output += '\n';
    }
  }

  // Add any remaining sections not in predefined order
  for (const sectionKey of Object.keys(sections)) {
    if (!sectionOrder.includes(sectionKey) && sections[sectionKey].length > 0) {
      const sectionTitle = getSectionTitle(sectionKey);
      output += `## ${sectionTitle}\n\n`;

      for (const page of sections[sectionKey]) {
        const desc = page.description ? `: ${page.description}` : '';
        output += `- [${page.title}](${page.mdUrl})${desc}\n`;
      }

      output += '\n';
    }
  }

  return output;
}

/**
 * Generate llms-full.txt (complete content)
 */
function generateLLMSFullTxt(sections, sectionOrder) {
  let output = `# ${CONFIG.siteTitle}\n\n`;
  output += `> ${CONFIG.siteDescription}\n\n`;
  output += `This file contains the complete content of the Gatling documentation.\n\n`;
  output += `---\n\n`;

  // Add sections in defined order
  for (const sectionKey of sectionOrder) {
    if (sections[sectionKey] && sections[sectionKey].length > 0) {
      const sectionTitle = getSectionTitle(sectionKey);
      output += `## ${sectionTitle}\n\n`;

      for (const page of sections[sectionKey]) {
        output += generatePageContent(page);
      }
    }
  }

  // Add any remaining sections
  for (const sectionKey of Object.keys(sections)) {
    if (!sectionOrder.includes(sectionKey) && sections[sectionKey].length > 0) {
      const sectionTitle = getSectionTitle(sectionKey);
      output += `## ${sectionTitle}\n\n`;

      for (const page of sections[sectionKey]) {
        output += generatePageContent(page);
      }
    }
  }

  return output;
}

/**
 * Generate formatted content for a single page
 */
function generatePageContent(page) {
  let output = `### ${page.title}\n\n`;

  if (page.description) {
    output += `**Description:** ${page.description}\n\n`;
  }

  output += `**URL:** ${page.url}\n\n`;
  output += page.content;
  output += `\n\n---\n\n`;

  return output;
}

/**
 * Main execution
 */
function main() {
  console.log('Generating llms.txt files for Gatling documentation...\n');

  // Get section order from root _index.md
  const sectionOrder = getSectionOrder();
  console.log(`Section order: ${sectionOrder.join(', ')}`);

  // Get all content files
  const files = getMarkdownFiles(CONFIG.contentDir);
  console.log(`Found ${files.length} content files`);

  if (files.length === 0) {
    console.error('No content files found. Check your content directory.');
    process.exit(1);
  }

  // Parse content files
  const { sections, allPages } = parseContentFiles(files);
  console.log(`Organized into ${Object.keys(sections).length} sections`);

  // Ensure output directory exists
  if (!fs.existsSync(CONFIG.outputDir)) {
    fs.mkdirSync(CONFIG.outputDir, { recursive: true });
  }

  // Generate llms.txt
  const llmsTxt = generateLLMSTxt(sections, sectionOrder);
  const llmsTxtPath = path.join(CONFIG.outputDir, 'llms.txt');
  fs.writeFileSync(llmsTxtPath, llmsTxt, 'utf8');
  const llmsTxtSize = (llmsTxt.length / 1024).toFixed(2);

  // Generate llms-full.txt
  const llmsFullTxt = generateLLMSFullTxt(sections, sectionOrder);
  const llmsFullTxtPath = path.join(CONFIG.outputDir, 'llms-full.txt');
  fs.writeFileSync(llmsFullTxtPath, llmsFullTxt, 'utf8');
  const llmsFullTxtSize = (llmsFullTxt.length / 1024).toFixed(2);

  // Statistics
  const pagesWithoutDescription = allPages.filter(p => !p.description).length;

  console.log('\nStatistics:');
  console.log(`  Total pages: ${allPages.length}`);
  console.log(`  Total sections: ${Object.keys(sections).length}`);
  console.log(`  Pages without descriptions: ${pagesWithoutDescription}`);

  console.log('\nSection breakdown:');
  for (const [section, pages] of Object.entries(sections)) {
    console.log(`  ${section}: ${pages.length} pages`);
  }

  console.log('\nGenerated files:');
  console.log(`  ${llmsTxtPath} (${llmsTxtSize} KB)`);
  console.log(`  ${llmsFullTxtPath} (${llmsFullTxtSize} KB)`);

  if (pagesWithoutDescription > 0) {
    console.log(`\nWarning: ${pagesWithoutDescription} pages are missing descriptions.`);
  }

  console.log('\nDone!');
}

main();
