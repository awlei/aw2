# V2rayNG - Code Quality Configuration
# This document outlines the code quality standards and best practices for the project.

## Code Style Standards

### Kotlin
- Use 4 spaces for indentation (no tabs)
- Maximum line length: 150 characters
- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Prefer val over var when possible
- Use data classes for data holding
- Use extension functions for utility methods

### Java
- Follow Java naming conventions
- Use camelCase for variables and methods
- Use PascalCase for classes
- Use UPPER_SNAKE_CASE for constants

## Documentation

### File Header
All Kotlin files should include a file header comment:
```kotlin
/**
 * [Brief description of the file's purpose]
 *
 * [More detailed description if needed]
 *
 * @author [Author name]
 * @since [Date or version]
 */
```

### Function Documentation
Public functions should include KDoc comments:
```kotlin
/**
 * [Brief description of what the function does]
 *
 * @param paramName [Description of the parameter]
 * @return [Description of the return value]
 * @throws [Exception] [When and why this exception is thrown]
 */
```

### Class Documentation
Public classes should include KDoc comments:
```kotlin
/**
 * [Brief description of the class]
 *
 * [More detailed description of the class's purpose and usage]
 *
 * @property propertyName [Description of the property]
 */
```

## Code Organization

### Package Structure
- `com.v2ray.ang` - Main package
- `com.v2ray.ang.ui` - UI components (Activities, Fragments, Adapters)
- `com.v2ray.ang.viewmodel` - ViewModels for MVVM architecture
- `com.v2ray.ang.dto` - Data Transfer Objects
- `com.v2ray.ang.handler` - Business logic handlers
- `com.v2ray.ang.service` - Android services
- `com.v2ray.ang.util` - Utility functions and helper classes
- `com.v2ray.ang.extension` - Kotlin extension functions
- `com.v2ray.ang.receiver` - Broadcast receivers

### File Organization
Each file should contain one public class/interface/object
Use package-private access modifier for internal implementation details

## Best Practices

### Memory Management
- Avoid memory leaks by properly releasing resources
- Use weak references for long-lived objects
- Clear collections when no longer needed
- Unregister receivers and listeners in onDestroy/onCleared

### Threading
- Use coroutines for asynchronous operations
- Use appropriate dispatchers (IO, Main, Default)
- Avoid blocking the main thread
- Cancel coroutine jobs when no longer needed

### Error Handling
- Use try-catch blocks for exception handling
- Log errors with proper context
- Provide meaningful error messages to users
- Handle edge cases gracefully

### Security
- Validate user inputs
- Use secure network configurations
- Store sensitive data securely
- Use certificate pinning for network requests
- Avoid logging sensitive information

### Performance
- Optimize database queries
- Use caching where appropriate
- Avoid unnecessary object allocations
- Use efficient data structures
- Profile and optimize critical paths

## Testing

### Unit Tests
- Test individual functions and classes
- Use mocking for dependencies
- Test both success and failure cases
- Aim for high code coverage

### Integration Tests
- Test interactions between components
- Test with real implementations where possible
- Test UI interactions

### Code Review Checklist
- [ ] Code follows style guidelines
- [ ] Code is well-documented
- [ ] Error handling is appropriate
- [ ] No memory leaks
- [ ] No security vulnerabilities
- [ ] Performance is acceptable
- [ ] Tests are written and passing
