# ActionGlass 🪟

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java Version](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.oracle.com/java/)
[![Spigot Version](https://img.shields.io/badge/Spigot-1.20.4+-green.svg)](https://www.spigotmc.org/)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)](#)

**ActionGlass** is a realistic glass breaking mechanics plugin for Minecraft servers that adds cinematic, physics-based glass destruction. Experience movie-like action sequences as players crash through windows while flying with elytra, break glass walls while sprinting, or shatter glass floors when falling at high speeds!

## 🎬 Features

### 🏃‍♂️ Movement-Based Breaking
- **Elytra Breaking**: Crash through glass structures while gliding at high speeds
- **Sprint Breaking**: Run through glass walls and barriers 
- **Fall Breaking**: Smash through glass floors when falling from heights
- **Speed-Based**: Only break glass when moving fast enough for realistic physics

### 🏹 Projectile Breaking
- **Arrow Breaking**: Arrows create small, precise holes in glass
- **Trident Breaking**: Tridents shatter larger areas with more force
- **Wind Charge Breaking**: New 1.20.4+ wind charges create explosive glass breaks
- **Customizable Radius**: Different projectiles break different sized areas

### 🔄 Smart Regeneration
- **Automatic Restoration**: Broken glass regenerates after a configurable delay
- **Visual Effects**: Subtle particle effects when glass regenerates
- **Performance Optimized**: Efficient tracking of broken blocks

### 🛡️ Protection Integration
- **WorldGuard**: Respects region protections
- **GriefPrevention**: Won't break glass in claimed areas
- **Towny**: Honors town permissions
- **Factions**: Respects faction territory

### ⚡ Performance Features
- **Async Processing**: Non-blocking glass breaking calculations
- **Smart Cooldowns**: Prevents spam breaking and server lag
- **Configurable Limits**: Control maximum simultaneous breaks
- **Optimized Detection**: Efficient collision detection algorithms

## 📥 Installation

### Requirements
- **Java 17+** (Required for modern Minecraft versions)
- **Spigot/Paper 1.20.4+** (Recommended: Paper for better performance)
- **Optional**: WorldGuard, GriefPrevention, Towny, or Factions for protection integration

### Installation Steps

1. **Download** the latest ActionGlass.jar from [Releases](https://github.com/yourusername/ActionGlass/releases)

2. **Upload** the jar file to your server's `plugins/` directory

3. **Restart** your server (or use `/reload` if you must)

4. **Configure** the plugin by editing `plugins/ActionGlass/config.yml`

5. **Test** the plugin by flying into glass with elytra or shooting arrows at glass blocks

## ⚙️ Configuration

ActionGlass is highly configurable. Here's a breakdown of the main configuration sections:

### Basic Features
```yaml
features:
  arrow-breaking: true      # Enable projectile breaking
  elytra-breaking: true     # Enable elytra crash-through
  falling-breaking: true    # Enable fall-through breaking  
  running-breaking: true    # Enable sprint-through breaking
  regeneration: true        # Enable automatic glass restoration
```

### Speed & Timing
```yaml
settings:
  minimum-speed: 0.3              # Speed required to break glass
  glass-break-cooldown: 500       # Cooldown between breaks (ms)
  regeneration-delay: 6000        # Time before glass regenerates (ms)
```

### Break Patterns
```yaml
break-radius:
  arrow: 1          # Small precise holes
  trident: 2        # Medium impact craters  
  wind-charge: 3    # Large explosive breaks
  elytra: 2         # Medium crash-through patterns
  falling: 2        # Medium impact from falls
  running: 1        # Small breaks from running
  sprinting: 2      # Larger breaks from sprinting
```

### Advanced Settings
```yaml
advanced:
  debug-mode: false                    # Enable debug logging
  max-simultaneous-breaks: 50          # Performance limit
  break-particles: true                # Visual break effects
  break-sounds: true                   # Audio break effects
  regeneration-particles: true         # Visual regen effects
  regeneration-sounds: true            # Audio regen effects
  min-break-distance: 1.0             # Prevent chain reactions
```

### World Management
```yaml
worlds:
  enabled-worlds: []                   # Specific worlds only (empty = all)
  disabled-worlds:                     # Worlds to exclude
    - "creative_world"
    - "build_world"
```

### Glass Type Customization
```yaml
glass-types:
  glass:
    enabled: true
    break-resistance: 1.0              # Normal difficulty
    regeneration-time: 1.0             # Normal regen speed
  
  glass-panes:
    enabled: true  
    break-resistance: 0.7              # Easier to break (thinner)
    regeneration-time: 0.8             # Faster regeneration
```

### Player Groups
```yaml
player-groups:
  default:
    break-multiplier: 1.0              # Normal breaking ability
    cooldown-multiplier: 1.0           # Normal cooldown
    
  vip:                                 # Requires permission: actionglass.vip
    break-multiplier: 1.2              # 20% easier breaking
    cooldown-multiplier: 0.8           # 20% shorter cooldown
    
  admin:                               # Requires permission: actionglass.admin  
    break-multiplier: 2.0              # Much easier breaking
    cooldown-multiplier: 0.5           # Much shorter cooldown
```

## 🎮 Usage

### For Players

#### Elytra Breaking
1. Equip elytra and rockets
2. Launch into flight
3. Build up speed (the faster, the better!)
4. Fly directly into glass structures
5. Watch as you crash through in cinematic style!

#### Sprint Breaking  
1. Start sprinting (double-tap W or hold Ctrl)
2. Run directly at glass walls or barriers
3. Break through at full speed
4. Works best with thin glass structures (1 block thick)

#### Fall Breaking
1. Jump or fall from a significant height
2. Land on or fall through glass structures
3. High-speed impacts will shatter the glass
4. Great for dramatic entrances!

#### Projectile Breaking
1. Shoot arrows, throw tridents, or use wind charges
2. Aim at glass blocks or structures  
3. Different projectiles create different break patterns
4. Experiment with angles and distances!

### For Administrators

#### Commands
```
/actionglass help                    # Show all available commands
/actionglass status                  # View plugin status and statistics
/actionglass reload                  # Reload configuration from file
/actionglass regenerate              # Instantly regenerate all broken glass
/actionglass toggle <feature>        # Toggle features on/off temporarily
```

#### Permissions
```
actionglass.*                        # All permissions (for admins)
actionglass.reload                   # Permission to reload config
actionglass.status                   # Permission to view status
actionglass.regenerate               # Permission to regenerate glass
actionglass.toggle                   # Permission to toggle features
actionglass.vip                      # VIP player benefits
actionglass.admin                    # Admin player benefits
```

## 🔧 Advanced Configuration

### Performance Tuning

For **large servers** (100+ players):
```yaml
settings:
  glass-break-cooldown: 1000         # Longer cooldown
advanced:
  max-simultaneous-breaks: 25        # Lower limit
  async-processing: true             # Enable async
  max-blocks-per-tick: 5             # Process fewer blocks per tick
```

For **small servers** (< 50 players):
```yaml
settings:
  glass-break-cooldown: 250          # Shorter cooldown  
advanced:
  max-simultaneous-breaks: 100       # Higher limit
  max-blocks-per-tick: 20            # Process more blocks per tick
```

### Realistic Physics Settings

For **movie-like action**:
```yaml
settings:
  minimum-speed: 0.2                 # Easier to break glass
break-radius:
  elytra: 3                          # Larger crash patterns
  sprinting: 3                       # Bigger sprint breaks
  falling: 3                         # Dramatic fall impacts
```

For **realistic physics**:
```yaml
settings:
  minimum-speed: 0.5                 # Need more speed
break-radius:
  elytra: 1                          # Smaller, precise breaks
  sprinting: 1                       # Minimal sprint breaking
  falling: 2                         # Moderate fall damage
```

### Protection Plugin Integration

#### WorldGuard Setup
1. Install WorldGuard on your server
2. ActionGlass will automatically detect it
3. Glass won't break in protected regions
4. Configure in config.yml:
```yaml
integrations:
  worldguard:
    enabled: true
    respect-regions: true
```

#### GriefPrevention Setup  
1. Install GriefPrevention
2. ActionGlass respects all claims automatically
3. Players can't break glass in others' claims
```yaml
integrations:
  griefprevention:
    enabled: true
    respect-claims: true
```

## 🎯 Use Cases & Examples

### 🏰 Medieval Servers
- Castle siege scenarios with arrow breaking
- Dramatic entrances through stained glass windows
- Realistic combat with projectile glass breaking

### 🏙️ Modern/City Servers  
- Superhero-style building crashes with elytra
- Bank heist scenarios breaking through skylights
- Parkour courses with glass-breaking elements

### 🎮 Minigame Servers
- Glass-breaking parkour challenges
- Target practice with different projectiles
- Racing courses with glass obstacles

### 🏗️ Creative Servers
- Movie scene recreation with realistic glass physics
- Architecture showcases with breakable elements
- Interactive builds with temporary glass barriers

## 🐛 Troubleshooting

### Common Issues

#### Glass Not Breaking
**Problem**: Players can't break glass despite high speed
**Solutions**:
- Check if the feature is enabled in config.yml
- Verify minimum-speed setting isn't too high
- Ensure the player has necessary permissions
- Check if the area is protected by WorldGuard/other plugins

#### Performance Issues
**Problem**: Server lag when breaking glass
**Solutions**:
- Reduce `max-simultaneous-breaks` in config
- Increase `glass-break-cooldown` 
- Enable `async-processing`
- Lower `max-blocks-per-tick`

#### Glass Not Regenerating
**Problem**: Broken glass doesn't restore
**Solutions**:
- Verify `regeneration: true` in config
- Check `regeneration-delay` setting
- Ensure server isn't restarting before regen time
- Use `/actionglass regenerate` to force restoration

#### Plugin Conflicts
**Problem**: ActionGlass conflicts with other plugins
**Solutions**:
- Check plugin load order in server logs
- Disable conflicting features in other plugins
- Update all plugins to latest versions
- Contact support with specific error messages

### Debug Mode
Enable debug logging to troubleshoot issues:
```yaml
advanced:
  debug-mode: true
```

This will log detailed information about:
- Glass breaking attempts
- Speed calculations  
- Permission checks
- Protection plugin interactions

## 🔄 API for Developers

ActionGlass provides a comprehensive API for other plugin developers:

### Maven Dependency
```xml
<dependency>
    <groupId>com.actionglass</groupId>
    <artifactId>ActionGlass</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

### Basic API Usage
```java
// Get ActionGlass instance
ActionGlass actionGlass = (ActionGlass) Bukkit.getPluginManager().getPlugin("ActionGlass");

// Check if glass breaking is enabled
if (actionGlass.isElytraBreakingEnabled()) {
    // Your code here
}

// Get glass manager
GlassManager glassManager = actionGlass.getGlassManager();

// Break glass programmatically
Location glassLocation = new Location(world, x, y, z);
glassManager.breakGlassArea(glassLocation, 2);

// Check if a material is glass
if (glassManager.isGlass(Material.GLASS)) {
    // Handle glass block
}
```

### Events
ActionGlass fires custom events that other plugins can listen to:

```java
@EventHandler
public void onGlassBreak(GlassBreakEvent event) {
    Player player = event.getPlayer();
    Block glassBlock = event.getGlassBlock();
    GlassBreakCause cause = event.getCause(); // ELYTRA, ARROW, FALLING, etc.
    
    // Cancel the event to prevent breaking
    event.setCancelled(true);
}

@EventHandler  
public void onGlassRegenerate(GlassRegenerateEvent event) {
    Location location = event.getLocation();
    Material glassType = event.getGlassType();
    
    // Handle glass regeneration
}
```

## 📊 Statistics & Metrics

ActionGlass tracks various statistics that can be viewed with `/actionglass status`:

- **Total Glass Broken**: Lifetime count of broken glass blocks
- **Currently Broken**: Number of glass blocks awaiting regeneration  
- **Break Methods**: Breakdown by elytra, arrows, falling, etc.
- **Top Players**: Players who have broken the most glass
- **Performance Metrics**: Average processing times and server impact

## 🤝 Contributing

We welcome contributions! Here's how to get involved:

### Development Setup
1. **Clone** the repository:
```bash
git clone https://github.com/yourusername/ActionGlass.git
cd ActionGlass
```

2. **Build** the project:
```bash
mvn clean compile
```

3. **Run tests**:
```bash
mvn test
```

4. **Create** a test server:
```bash
mvn package
# Copy target/ActionGlass-1.0.0.jar to your test server
```

### Code Style
- Follow Java naming conventions
- Add Javadoc comments for public methods
- Include unit tests for new features
- Use meaningful variable and method names

### Submitting Changes
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 Changelog

### Version 1.0.0 (Latest)
- ✨ Initial release
- 🏃‍♂️ Movement-based glass breaking (elytra, falling, running)
- 🏹 Projectile glass breaking (arrows, tridents, wind charges)
- 🔄 Automatic glass regeneration system
- 🛡️ Protection plugin integration (WorldGuard, GriefPrevention, Towny, Factions)
- ⚡ Performance optimizations and async processing
- 🎨 Visual and audio effects for breaking and regeneration
- 🔧 Comprehensive configuration system
- 📊 Statistics and monitoring commands
- 🎯 Player group permissions and multipliers

### Planned Features (v1.1.0)
- 🔥 Fire-based glass breaking (fire charges, blazes)
- 🧨 Explosion-based breaking (TNT, creepers)
- 🌊 Water/ice interaction effects
- 📱 Web dashboard for server statistics
- 🎨 Custom break
