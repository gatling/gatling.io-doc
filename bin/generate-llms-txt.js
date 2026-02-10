#!/usr/bin/env node

const fs = require('fs');
const path = require('path');
const yaml = require('yaml');
const glob = require('glob');
const matter = require('gray-matter');

// Configuration
const CONFIG = {
  contentDir: 'content',
  outputDir: 'public',
  baseUrl: 'https://docs.gatling.io',
  siteTitle: 'Gatling Documentation',
  siteDescription: 'Modern load testing framework for testing the performance of web applications'
};

// Section order and custom titles
const SECTION_ORDER = [
  'getting-started',
  'tutorials',
  'guides',
  'core-concepts',
  'reference',
  'api',
  'examples',
  'migration',
  'faq'
];

const SECTION_TITLES = {
  'getting-started': 'Getting Started',
  'tutorials': 'Tutorials',
  'guides': 'User Guides',
  'core-concepts': 'Core Concepts',
  'reference': 'Reference Documentation',
  'api': 'API Documentation',
  'examples': 'Examples',
  'migration': 'Migration Guides',
  'faq': 'FAQ'
};

/**
 * Extract code section marked with comment brackets
 * Handles patterns like //#sectionName ... //#sectionName
 */
function extractCodeSection(fileContent, sectionName, fileExt) {
  // Determine comment style based on file extension
  const commentMap = {
    'java': '//',
    'js': '//',
    'ts': '//',
    'scala': '//',
    'kt': '//',
    'py': '#',
    'rb': '#',
    'sh': '#'
  };
  
  const comment = commentMap[fileExt] || '//';
  
  // Create regex to match //#sectionName ... //#sectionName
  const startMarker = `${comment}#${sectionName}`;

  // Global regexes might return multiple results with common prefixes: e.g: binaryHeader/binaryHeaders, etc.
  const pattern = `${escapeRegex(startMarker)}\\n((.|\\n)+?)${escapeRegex(startMarker)}`;
  const matches = [...fileContent.matchAll(pattern)];

  if (matches.length === 0 || matches[0].length < 2) {
    return null;
  }
  
  return matches[0][1].trim();
}

/**
 * Escape special regex characters
 */
function escapeRegex(str) {
  return str.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

/**
 * Expand include-code shortcodes with actual code content
 */
function expandCodeShortcodes(content, filePath) {
  const dir = path.dirname(filePath);
  const codeDir = path.join(dir, 'code');
  
  if (!fs.existsSync(codeDir)) {
    return content;
  }
  
  // Match {{< include-code "name" >}} patterns
  const shortcodeRegex = /\{\{<\s*include-code\s+"([^"]+)"\s*>\}\}/g;
  
  return content.replace(shortcodeRegex, (match, codeName) => {
    try {
      const allFiles = fs.readdirSync(codeDir);
      
      // Filter for code files (not images, etc.)
      const codeFiles = allFiles.filter(file => {
        const ext = path.extname(file).slice(1);
        return ['java', 'js', 'ts', 'scala', 'kt', 'py', 'rb', 'sh'].includes(ext);
      });
      
      if (codeFiles.length === 0) {
        console.warn(`Warning: No code files found in ${codeDir}`);
        return `\n*[Code example: ${codeName}]*\n`;
      }
      
      // Extract the code section from each file
      const extractedSections = [];
      
      codeFiles.forEach(file => {
        const ext = path.extname(file).slice(1);
        const fullPath = path.join(codeDir, file);
        const fileContent = fs.readFileSync(fullPath, 'utf8');
        
        const codeSection = extractCodeSection(fileContent, codeName, ext);
        
        if (codeSection) {
          // Determine language for syntax highlighting
          const langMap = {
            'java': 'java',
            'js': 'javascript',
            'ts': 'typescript',
            'scala': 'scala',
            'kt': 'kotlin',
            'py': 'python',
            'rb': 'ruby',
            'sh': 'bash'
          };
          const lang = langMap[ext] || ext;
          
          // Extract language name from filename (e.g., "Java" from "HttpCheckSampleJava.java")
          const langName = file.replace(/Sample|Example|\..*$/gi, '').match(/[A-Z][a-z]+$/)?.[0] || lang;
          
          extractedSections.push({
            language: langName,
            lang: lang,
            code: codeSection
          });
        }
      });
      
      if (extractedSections.length === 0) {
        console.warn(`Warning: Code section "${codeName}" not found in any file in ${codeDir}`);
        console.warn(`  Files checked: ${codeFiles.join(', ')}`);
        return `\n*[Code example: ${codeName}]*\n`;
      }
      
      // Format the output with language tabs (markdown style)
      return '\n' + extractedSections.map(section => {
        return `**${section.language}**:\n\`\`\`${section.lang}\n${section.code}\n\`\`\``;
      }).join('\n\n') + '\n';
      
    } catch (error) {
      console.warn(`Warning: Could not process code for ${codeName}:`, error.message);
      return match;
    }
  });
}

/**
 * Expand alert shortcodes with markdown equivalents
 */
function expandAlertShortcodes(content) {
  // Match {{< alert type >}} content {{< /alert >}}
  const alertRegex = /\{\{<\s*alert\s+(\w+)\s*>\}\}([\s\S]*?)\{\{<\s*\/alert\s*>\}\}/g;
  
  return content.replace(alertRegex, (match, type, alertContent) => {
    const emoji = {
      'tip': '💡',
      'warning': '⚠️',
      'info': 'ℹ️',
      'note': '📝',
      'danger': '🚨'
    }[type.toLowerCase()] || '📌';
    
    return `\n> ${emoji} **${type.toUpperCase()}**: ${alertContent.trim()}\n`;
  });
}

/**
 * Extract frontmatter from markdown file using gray-matter
 */
function parseFrontmatter(filePath) {
  try {
    const fileContent = fs.readFileSync(filePath, 'utf8');
    const parsed = matter(fileContent);
    
    // Expand shortcodes in content
    let expandedContent = parsed.content;
    expandedContent = expandCodeShortcodes(expandedContent, filePath);
    expandedContent = expandAlertShortcodes(expandedContent);
    
    return {
      frontmatter: parsed.data,
      content: expandedContent
    };
  } catch (error) {
    console.warn(`Warning: Could not parse ${filePath}:`, error.message);
    return { frontmatter: {}, content: '' };
  }
}

/**
 * Get all markdown files from content directory
 */
function getContentFiles() {
  const pattern = path.join(CONFIG.contentDir, '**/*.md');
  return glob.sync(pattern, {
    ignore: [
      '**/node_modules/**',
      '**/_*.md' // Ignore partial files
    ]
  });
}

/**
 * Convert file path to URL path
 */
function filePathToUrl(filePath, addMdExtension = false) {
  const relativePath = path.relative(CONFIG.contentDir, filePath);
  let urlPath = relativePath
    .replace(/\.md$/, '')
    .replace(/\\/g, '/')
    .replace(/\/index$/, ''); // Remove /index for cleaner URLs
  
  if (urlPath === 'index' || urlPath === '') {
    urlPath = '';
  }
  
  const extension = addMdExtension ? '/index.md' : '';
  return urlPath ? `${CONFIG.baseUrl}/${urlPath}${extension}` : CONFIG.baseUrl;
}

/**
 * Parse all content files and organize by section
 */
function parseContentFiles(files) {
  const sections = {};
  const allPages = [];

  files.forEach(file => {
    const { frontmatter, content } = parseFrontmatter(file);
    
    // Skip files without titles or draft files
    if (!frontmatter.title || frontmatter.draft === true) {
      return;
    }

    const relativePath = path.relative(CONFIG.contentDir, file);
    const pathParts = relativePath.split(path.sep);
    const section = pathParts[0] || 'other';

    // Generate URLs
    const url = filePathToUrl(file, false);
    const mdUrl = filePathToUrl(file, true);

    const pageData = {
      title: frontmatter.title,
      description: frontmatter.description || '',
      url: url,
      mdUrl: mdUrl,
      weight: frontmatter.weight || 999,
      section: section,
      content: content.trim(),
      filePath: file
    };

    // Add to sections
    if (!sections[section]) {
      sections[section] = [];
    }
    sections[section].push(pageData);
    allPages.push(pageData);
  });

  // Sort pages within each section by weight, then by title
  Object.keys(sections).forEach(section => {
    sections[section].sort((a, b) => {
      if (a.weight !== b.weight) {
        return a.weight - b.weight;
      }
      return a.title.localeCompare(b.title);
    });
  });

  return { sections, allPages };
}

/**
 * Generate llms.txt (index file)
 */
function generateLLMSTxt(sections) {
  let output = `# ${CONFIG.siteTitle}\n`;
  output += `> ${CONFIG.siteDescription}\n\n`;

  // Add sections in defined order
  SECTION_ORDER.forEach(sectionKey => {
    if (sections[sectionKey] && sections[sectionKey].length > 0) {
      const sectionTitle = SECTION_TITLES[sectionKey] || formatSectionName(sectionKey);
      output += `## ${sectionTitle}\n`;
      
      sections[sectionKey].forEach(page => {
        const desc = page.description ? `: ${page.description}` : '';
        output += `- [${page.title}](${page.mdUrl})${desc}\n`;
      });
      
      output += '\n';
    }
  });

  // Add any remaining sections not in predefined order
  Object.keys(sections).forEach(sectionKey => {
    if (!SECTION_ORDER.includes(sectionKey) && sections[sectionKey].length > 0) {
      const sectionTitle = formatSectionName(sectionKey);
      output += `## ${sectionTitle}\n`;
      
      sections[sectionKey].forEach(page => {
        const desc = page.description ? `: ${page.description}` : '';
        output += `- [${page.title}](${page.mdUrl})${desc}\n`;
      });
      
      output += '\n';
    }
  });

  // Add optional section for meta pages
  output += `## Optional\n`;
  output += `- [Changelog](${CONFIG.baseUrl}/changelog): Release notes and version history\n`;
  output += `- [Community](${CONFIG.baseUrl}/community): Join the Gatling community\n`;
  output += `- [Support](${CONFIG.baseUrl}/support): Get help with Gatling\n`;

  return output;
}

/**
 * Generate llms-full.txt (complete content)
 */
function generateLLMSFullTxt(sections) {
  let output = `# ${CONFIG.siteTitle}\n`;
  output += `> ${CONFIG.siteDescription}\n\n`;
  output += `This file contains the complete content of the Gatling documentation.\n\n`;
  output += `---\n\n`;

  // Add sections in defined order
  SECTION_ORDER.forEach(sectionKey => {
    if (sections[sectionKey] && sections[sectionKey].length > 0) {
      const sectionTitle = SECTION_TITLES[sectionKey] || formatSectionName(sectionKey);
      output += `## ${sectionTitle}\n\n`;
      
      sections[sectionKey].forEach(page => {
        output += generatePageContent(page);
      });
    }
  });

  // Add any remaining sections
  Object.keys(sections).forEach(sectionKey => {
    if (!SECTION_ORDER.includes(sectionKey) && sections[sectionKey].length > 0) {
      const sectionTitle = formatSectionName(sectionKey);
      output += `## ${sectionTitle}\n\n`;
      
      sections[sectionKey].forEach(page => {
        output += generatePageContent(page);
      });
    }
  });

  return output;
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
 * Generate formatted content for a single page
 */
function generatePageContent(page) {
  let output = `### ${page.title}\n\n`;
  
  if (page.description) {
    output += `**Description:** ${page.description}\n\n`;
  }
  
  output += `**URL:** ${page.url}\n\n`;
  
  // Add the actual content
  output += page.content;
  
  // Add separators
  output += `\n\n---\n\n`;
  
  return output;
}

/**
 * Write file with error handling
 */
function writeOutputFile(filePath, content) {
  try {
    const dir = path.dirname(filePath);
    if (!fs.existsSync(dir)) {
      fs.mkdirSync(dir, { recursive: true });
    }
    fs.writeFileSync(filePath, content, 'utf8');
    return true;
  } catch (error) {
    console.error(`Error writing ${filePath}:`, error.message);
    return false;
  }
}

/**
 * Generate statistics
 */
function generateStats(sections, allPages, llmsTxtContent, llmsFullTxtContent) {
  const stats = {
    totalPages: allPages.length,
    totalSections: Object.keys(sections).length,
    llmsTxtSize: (llmsTxtContent.length / 1024).toFixed(2),
    llmsFullTxtSize: (llmsFullTxtContent.length / 1024).toFixed(2),
    pagesWithoutDescription: allPages.filter(p => !p.description).length,
    sectionBreakdown: {}
  };

  Object.keys(sections).forEach(section => {
    stats.sectionBreakdown[section] = sections[section].length;
  });

  return stats;
}

/**
 * Main execution
 */
function main() {
  console.log('🚀 Generating llms.txt files for Gatling documentation...\n');

  // Get all content files
  const files = getContentFiles();
  console.log(`📄 Found ${files.length} content files`);

  if (files.length === 0) {
    console.error('❌ No content files found. Check your content directory.');
    process.exit(1);
  }

  // Parse content files
  const { sections, allPages } = parseContentFiles(files);
  console.log(`📁 Organized into ${Object.keys(sections).length} sections`);

  // Generate llms.txt
  const llmsTxt = generateLLMSTxt(sections);
  const llmsTxtPath = path.join(CONFIG.outputDir, 'llms.txt');
  const llmsTxtSuccess = writeOutputFile(llmsTxtPath, llmsTxt);

  // Generate llms-full.txt
  const llmsFullTxt = generateLLMSFullTxt(sections);
  const llmsFullTxtPath = path.join(CONFIG.outputDir, 'llms-full.txt');
  const llmsFullTxtSuccess = writeOutputFile(llmsFullTxtPath, llmsFullTxt);

  // Generate and display statistics
  const stats = generateStats(sections, allPages, llmsTxt, llmsFullTxt);

  console.log('\n📊 Statistics:');
  console.log(`   Total pages: ${stats.totalPages}`);
  console.log(`   Total sections: ${stats.totalSections}`);
  console.log(`   Pages without descriptions: ${stats.pagesWithoutDescription}`);
  console.log('\n📦 Section breakdown:');
  Object.entries(stats.sectionBreakdown).forEach(([section, count]) => {
    console.log(`   ${section}: ${count} pages`);
  });

  console.log('\n✅ Results:');
  if (llmsTxtSuccess) {
    console.log(`   ✓ Generated ${llmsTxtPath} (${stats.llmsTxtSize} KB)`);
  } else {
    console.log(`   ✗ Failed to generate ${llmsTxtPath}`);
  }

  if (llmsFullTxtSuccess) {
    console.log(`   ✓ Generated ${llmsFullTxtPath} (${stats.llmsFullTxtSize} KB)`);
  } else {
    console.log(`   ✗ Failed to generate ${llmsFullTxtPath}`);
  }

  // Warn about missing descriptions
  if (stats.pagesWithoutDescription > 0) {
    console.log(`\n⚠️ Warning: ${stats.pagesWithoutDescription} pages are missing descriptions.`);
    console.log('   Consider adding "description" field to frontmatter for better LLM context.');
  }

  // Exit with error if generation failed
  if (!llmsTxtSuccess || !llmsFullTxtSuccess) {
    process.exit(1);
  }

  console.log('\n✨ Done!\n');
}

// Run the script
if (require.main === module) {
  main();
}

module.exports = { generateLLMSTxt, generateLLMSFullTxt, parseContentFiles };
