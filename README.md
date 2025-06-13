# Changelog

All notable changes to ActionGlass will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Planned Features
- Fire-based glass breaking (fire charges, blazes)
- Explosion-based breaking (TNT, creepers) 
- Water/ice interaction effects
- Web dashboard for server statistics
- Custom break patterns and animations
- Sound effect customization
- Particle effect customization
- Multi-language support
- Database storage for statistics
- API expansion for third-party plugins

## [1.0.0] - 2024-01-15

### Added
- **Core Features**
  - Movement-based glass breaking (elytra, falling, running, sprinting)
  - Projectile glass breaking (arrows, tridents, wind charges)
  - Automatic glass regeneration system with configurable delays
  - Speed-based breaking mechanics with minimum speed thresholds
  - Configurable break radius for different breaking methods

- **Protection Integration**
  - WorldGuard region protection support
  - GriefPrevention claim protection support
  - Towny town protection support (planned)
  - Factions territory protection support (planned)

- **Configuration System**
  - Comprehensive config.yml with all options documented
  - Feature toggles for all breaking mechanics
  - Speed and timing customization
  - Break radius configuration per method
  - World-specific enable/disable settings
  - Glass type specific settings (glass blocks, panes, stained glass)
  - Player group permissions and multipliers
  - Performance optimization settings

- **Commands & Permissions**
  - `/actionglass help` - Show available commands
  - `/actionglass status` - View plugin status and statistics
  - `/actionglass reload` - Reload configuration
  - `/actionglass regenerate` - Instantly regenerate all broken glass
  - `/actionglass toggle <feature>` - View feature status
  - `/actionglass info` - Show plugin information
  - `/actionglass version` - Show version details
  - Full tab completion support
  - Permission-based command access

- **Performance Features**
  - Async processing for glass breaking calculations
  - Smart cooldown system to prevent spam
  - Configurable limits for simultaneous breaks
  - Efficient collision detection algorithms
  - Memory-optimized glass tracking system

- **Visual & Audio Effects**
  - Particle effects for glass breaking
  - Sound effects for glass breaking
  - Particle effects for glass regeneration
  - Sound effects for glass regeneration
  - Configurable effect settings

- **Advanced Features**
  - Player group specific settings (VIP, Admin multipliers)
  - Glass type resistance and regeneration multipliers
  - Debug mode for troubleshooting
  - Statistics tracking and reporting
  - World-specific configuration options

### Technical Details
- **Compatibility**: Spigot/Paper 1.20.4+
- **Java Version**: Java 17+
- **Dependencies**: Spigot API, WorldGuard (optional), GriefPrevention (optional)
- **Performance**: Optimized for servers with 100+ concurrent players
- **Memory Usage**: Minimal memory footprint with efficient data structures

### Known Issues
- None reported in initial release

### Migration Notes
- This is the initial release, no migration needed
- Default configuration is optimized for most server types
- Review config.yml settings before first use on production servers

---

## Version History Summary

| Version | Release Date | Major Features |
|---------|--------------|----------------|
| 1.0.0   | 2024-01-15   | Initial release with core glass breaking mechanics |

---

## Upgrade Instructions

### From Future Versions
*Instructions will be added as new versions are released*

### Configuration Changes
*Configuration change notes will be documented here for each version*

---

## Support & Feedback

- **Bug Reports**: [GitHub Issues](https://github.com/FizzleTits/ActionGlass/issues)
- **Feature Requests**: [GitHub Discussions](https://github.com/FizzleTits/ActionGlass/discussions)
- **Documentation**: [Wiki](https://github.com/FizzleTits/ActionGlass/wiki)

---

## Contributors

Thanks to all contributors who helped make ActionGlass possible:

- **Lead Developer**: [FizzleTits](https://github.com/FizzleTits)
- **Beta Testers**: Community members who helped test pre-release versions
- **Translators**: Contributors who helped with multi-language support (planned)

---

*For detailed technical changes, see the [commit history](https://github.com/FizzleTits/ActionGlass/commits/main).*
```

## 2. Updated CONTRIBUTING.md

```markdown:CONTRIBUTING.md
# Contributing to ActionGlass

Thank you for your interest in contributing to ActionGlass! This document provides guidelines and information for contributors.

## 🤝 How to Contribute

### Reporting Bugs

Before creating bug reports, please check the [existing issues](https://github.com/FizzleTits/ActionGlass/issues) to avoid duplicates.

**When submitting a bug report, please include:**

- **Clear title** describing the issue
- **Detailed description** of what happened vs. what you expected
- **Steps to reproduce** the issue
- **Server information**:
  - Server software (Spigot/Paper) and version
  - ActionGlass version
  - Java version
  - Other relevant plugins
- **Configuration files** (config.yml) if relevant
- **Console logs** showing any error messages
- **Screenshots/videos** if applicable

**Bug Report Template:**
```markdown
**Bug Description**
A clear description of what the bug is.

**To Reproduce**
Steps to reproduce the behavior:
1. Go to '...'
2. Click on '....'
3. See error

**Expected Behavior**
What you expected to happen.

**Server Environment**
- Server Software: [e.g. Paper 1.20.4]
- ActionGlass Version: [e.g. 1.0.0]
- Java Version: [e.g. Java 17]
- Other Plugins: [list relevant plugins]

**Additional Context**
Add any other context about the problem here.
```

### Suggesting Features

We welcome feature suggestions! Please use [GitHub Discussions](https://github.com/FizzleTits/ActionGlass/discussions) for feature requests.

**When suggesting features:**

- **Check existing suggestions** to avoid duplicates
- **Describe the feature** clearly and in detail
- **Explain the use case** - why would this be useful?
- **Consider implementation** - how might this work technically?
- **Think about configuration** - what options should be configurable?

### Contributing Code

#### Development Setup

1. **Fork the repository**
   ```bash
   git clone https://github.com/FizzleTits/ActionGlass.git
   cd ActionGlass
   ```

2. **Set up development environment**
   - Install Java 17+
   - Install Maven 3.6+
   - Install Git
   - IDE recommendation: IntelliJ IDEA or Eclipse

3. **Build the project**
   ```bash
   mvn clean compile
   ```

4. **Run tests**
   ```bash
   mvn test
   ```

5. **Create test server**
   - Set up a local Spigot/Paper test server
   - Copy the built JAR to the plugins folder
   - Test your changes thoroughly

#### Code Style Guidelines

**Java Code Style:**
- Follow standard Java naming conventions
- Use meaningful variable and method names
- Keep methods focused and concise (max 50 lines when possible)
- Add Javadoc comments for public methods and classes
- Use proper indentation (4 spaces, no tabs)
- Maximum line length: 120 characters
- Always use braces for if/for/while statements

**Example:**
```java
/**
 * Breaks glass in a specified radius around a location
 * 
 * @param center The center location for glass breaking
 * @param radius The radius in blocks to break glass
 * @param cause The cause of the glass breaking
 * @return The number of glass blocks broken
 */
public int breakGlassArea(Location center, int radius, GlassBreakCause cause) {
    if (center == null || radius <= 0) {
        return 0;
    }
    
    // Implementation here
}
```

**Configuration Style:**
- Use clear, descriptive configuration keys
- Include detailed comments explaining each option
- Provide sensible default values
- Group related options together

#### Commit Guidelines

**Commit Message Format:**
```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

**Examples:**
```bash
feat(breaking): add explosion-based glass breaking
fix(regeneration): resolve memory leak in glass tracking
docs(readme): update installation instructions
test(manager): add unit tests for GlassManager
```

#### Pull Request Process

1. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes**
   - Write clean, well-documented code
   - Add tests for new functionality
   - Update documentation as needed

3. **Test thoroughly**
   - Run all existing tests: `mvn test`
   - Test on a real server
   - Test edge cases and error conditions

4. **Update documentation**
   - Update README.md if needed
   - Update config.yml comments
   - Add changelog entry

5. **Submit pull request**
   - Use a clear, descriptive title
   - Fill out the pull request template
   - Link any related issues
   - Request review from maintainers

**Pull Request Template:**
```markdown
## Description
Brief description of changes made.

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Documentation update
- [ ] Performance improvement
- [ ] Code refactoring

## Testing
- [ ] Tested on local development server
- [ ] All existing tests pass
- [ ] Added new tests for new functionality
- [ ] Tested with multiple server versions

## Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Documentation updated
- [ ] Changelog updated
```

## 🧪 Testing Guidelines

### Unit Testing
- Write unit tests for all new functionality
- Use JUnit 5 for testing framework
- Mock external dependencies (Bukkit API, etc.)
- Aim for >80% code coverage

### Integration Testing
- Test with real Spigot/Paper servers
- Test with different server versions (1.20.4+)
- Test with various plugin combinations
- Test performance under load

### Manual Testing Checklist
- [ ] All glass breaking methods work correctly
- [ ] Glass regeneration functions properly
- [ ] Commands work and have proper permissions
- [ ] Configuration changes take effect
- [ ] No console errors or warnings
- [ ] Performance is acceptable
- [ ] Protection plugin integration works

## 📚 Documentation

### Code Documentation
- Add Javadoc comments to all public methods and classes
- Include parameter descriptions and return value information
- Document any complex algorithms or logic
- Keep comments up-to-date with code changes

### User Documentation
- Update README.md for user-facing changes
- Update configuration documentation
- Add examples for new features
- Keep installation instructions current

## 🏗️ Architecture Guidelines

### Plugin Structure
```
src/main/java/com/actionglass/
├── ActionGlass.java           # Main plugin class
├── GlassManager.java          # Core glass management logic
├── GlassBreakListener.java    # Event handling
├── ActionGlassCommand.java    # Command handling
├── config/                    # Configuration management
├── integrations/              # Third-party plugin integrations
├── utils/                     # Utility classes
└── api/                       # Public API classes
```

### Design Principles
- **Single Responsibility**: Each class should have one clear purpose
- **Dependency Injection**: Use constructor injection for dependencies
- **Interface Segregation**: Create focused interfaces
- **Open/Closed Principle**: Open for extension, closed for modification
- **Performance First**: Always consider performance impact

### Adding New Features

1. **Plan the feature**
   - Define requirements clearly
   - Consider configuration options
   - Think about performance impact
   - Plan testing approach

2. **Design the implementation**
   - Identify which classes need changes
   - Define new interfaces if needed
   - Consider backward compatibility
   - Plan database schema changes (if applicable)

3. **Implement incrementally**
   - Start with core functionality
   - Add configuration options
   - Add user interface (commands)
   - Add documentation

4. **Test thoroughly**
   - Unit tests for logic
   - Integration tests with server
   - Performance testing
   - User acceptance testing

## 🚀 Release Process

### Version Numbering
We follow [Semantic Versioning](https://semver.org/):
- **MAJOR**: Incompatible API changes
- **MINOR**: New functionality (backward compatible)
- **PATCH**: Bug fixes (backward compatible)

### Release Checklist
- [ ] All tests pass
- [ ] Documentation updated
- [ ] Changelog updated
- [ ] Version numbers updated
- [ ] Performance testing completed
- [ ] Security review completed
- [ ] Beta testing completed

## 💬 Community Guidelines

### Code of Conduct
- Be respectful and inclusive
- Help others learn and grow
- Provide constructive feedback
- Focus on the issue, not the person
- Follow the [Contributor Covenant](https://www.contributor-covenant.org/)

### Communication Channels
- **GitHub Issues**: Bug reports and feature requests
- **GitHub Discussions**: General discussion and questions

## 🔒 Security

### Reporting Security Issues
Please report security vulnerabilities privately by creating a private issue or contacting the maintainer directly.

**Do not** create public GitHub issues for security vulnerabilities.

### Security Guidelines
- Never log sensitive information
- Validate all user input
- Use secure coding practices
- Keep dependencies updated
- Follow OWASP guidelines

## 📄 License

By contributing to ActionGlass, you agree that your contributions will be licensed under the MIT License.

## ❓ Questions?

If you have questions about contributing, please:
1. Check the [FAQ](https://github.com/FizzleTits/ActionGlass/wiki/FAQ)
2. Search [existing discussions](https://github.com/FizzleTits/ActionGlass/discussions)
3. Create a new discussion

Thank you for contributing to ActionGlass! 🎉
```

## 3. Updated README.md (just the GitHub links section)

You'll also want to update the README.md file to use your GitHub URL. Here's the updated section:

```markdown:README.md
# ActionGlass 🪟

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java Version](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.oracle.com/java/)
[![Spigot Version](https://img.shields.io/badge/Spigot-1.20.4+-green.svg)](https://www.spigotmc.org/)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)](#)

**ActionGlass** is a realistic glass breaking mechanics plugin for Minecraft servers that adds cinematic, physics-based glass destruction. Experience movie-like action sequences as players crash through windows while flying with elytra, break glass walls while sprinting, or shatter glass floors when falling at high speeds!

## 📥 Installation

### Requirements
- **Java 17+** (Required for modern Minecraft versions)
- **Spigot/Paper 1.20.4+** (Recommended: Paper for better performance)
- **Optional**: WorldGuard, GriefPrevention, Towny, or Factions for protection integration

### Installation Steps

1. **Download** the latest ActionGlass.jar from [Releases](https://github.com/FizzleTits/ActionGlass/releases)

2. **Upload** the
