# Frontend Tests Documentation

This directory contains comprehensive TestFX-based tests for the Student Life Assistant frontend application.

## Overview

The frontend tests use TestFX framework to test JavaFX UI components and user interactions. These tests ensure that the application's user interface works correctly and provides a good user experience.

## Test Structure

### Controllers
- **LoginControllerTest**: Tests authentication, form validation, and navigation
- **SignupControllerTest**: Tests user registration, validation, and error handling
- **MainControllerTest**: Tests menu interactions, navigation, and UI functionality
- **CalendarControllerTest**: Tests event management and calendar interactions

### Views
- **CalendarViewTest**: Tests calendar display, event rendering, and user interactions

### Client
- **CalendarApiClientTest**: Tests API communication, error handling, and data validation

## Test Coverage Areas

### UI Component Testing
- Component creation and initialization
- Component visibility and accessibility
- Component state management
- Component responsiveness

### User Interaction Testing
- Mouse clicks and keyboard input
- Form field interactions
- Button clicks and navigation
- Menu interactions
- Dialog interactions

### Form Validation Testing
- Input validation (empty fields, invalid formats)
- Error message display
- Success message display
- Field clearing after successful operations

### Navigation Testing
- Scene transitions
- View switching
- Back navigation
- Menu navigation

### Error Handling Testing
- Network error handling
- API error responses
- Invalid input handling
- Exception handling

### API Integration Testing
- API call success scenarios
- API call failure scenarios
- Data serialization/deserialization
- Authentication token handling

### Accessibility Testing
- Keyboard navigation
- Tab order
- Screen reader compatibility
- Focus management

### Responsive Design Testing
- Window resizing
- Component scaling
- Layout adjustments

## Running the Tests

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- JavaFX 17

### Running All Frontend Tests
```bash
mvn test -Dtest=FrontendTestSuite
```

### Running Individual Test Classes
```bash
# Login tests
mvn test -Dtest=LoginControllerTest

# Signup tests
mvn test -Dtest=SignupControllerTest

# Main controller tests
mvn test -Dtest=MainControllerTest

# Calendar controller tests
mvn test -Dtest=CalendarControllerTest

# Calendar view tests
mvn test -Dtest=CalendarViewTest

# API client tests
mvn test -Dtest=CalendarApiClientTest
```

### Running Specific Test Methods
```bash
# Run a specific test method
mvn test -Dtest=LoginControllerTest#testLoginWithValidCredentials

# Run multiple test methods
mvn test -Dtest=LoginControllerTest#testLoginWithValidCredentials,testLoginWithInvalidCredentials
```

### Headless Testing (for CI/CD)
```bash
# Run tests in headless mode
mvn test -Dtestfx.headless=true

# Run with specific display
mvn test -Dtestfx.headless=true -Dtestfx.display=:99
```

## TestFX Configuration

### Dependencies
The tests use the following TestFX dependencies:
- `testfx-core`: Core TestFX functionality
- `testfx-junit5`: JUnit 5 integration
- `junit-jupiter`: JUnit 5 testing framework
- `mockito-core`: Mocking framework
- `hamcrest`: Assertion matchers

### Configuration
Tests are configured with:
- `ApplicationExtension`: Provides JavaFX test support
- `@Start`: Initializes the JavaFX application
- `@BeforeEach`/`@AfterEach`: Setup and cleanup
- Mocked static methods for API calls

### Test Structure
Each test class follows this structure:
```java
@ExtendWith(ApplicationExtension.class)
class ControllerTest {
    
    @Start
    private void start(Stage stage) {
        // Initialize JavaFX components
    }
    
    @BeforeEach
    void setUp() {
        // Setup mocks and test data
    }
    
    @AfterEach
    void tearDown() {
        // Cleanup resources
    }
    
    @Test
    void testMethod(FxRobot robot) {
        // Test implementation
    }
}
```

## Test Categories

### Unit Tests
- Component creation and initialization
- Method functionality
- Data validation
- Error handling

### Integration Tests
- API communication
- Database interactions
- Component interactions
- Navigation flows

### UI Tests
- User interactions
- Visual feedback
- Accessibility features
- Responsive design

### End-to-End Tests
- Complete user workflows
- Multi-step processes
- Cross-component functionality

## Best Practices

### Test Organization
- Group related tests together
- Use descriptive test method names
- Follow AAA pattern (Arrange, Act, Assert)
- Keep tests independent and isolated

### Mocking Strategy
- Mock external dependencies (APIs, databases)
- Use static mocking for utility classes
- Mock file system operations
- Mock network calls

### Assertions
- Use specific assertions for better error messages
- Test both positive and negative scenarios
- Verify UI state changes
- Check error message content

### Test Data
- Use realistic test data
- Create helper methods for common data setup
- Use constants for repeated values
- Clean up test data after tests

### Performance
- Keep tests fast and focused
- Avoid unnecessary UI rendering
- Use headless mode for CI/CD
- Minimize external dependencies

## Troubleshooting

### Common Issues

#### TestFX Headless Mode Issues
```bash
# Set display for headless testing
export DISPLAY=:99
Xvfb :99 -screen 0 1024x768x24 &
```

#### JavaFX Module Issues
```bash
# Add JavaFX modules to JVM arguments
--add-modules javafx.controls,javafx.fxml
```

#### Memory Issues
```bash
# Increase heap size for tests
mvn test -DargLine="-Xmx2g"
```

#### Test Isolation Issues
- Ensure tests don't share state
- Use `@AfterEach` for cleanup
- Mock static methods properly
- Reset mocks between tests

### Debugging Tests
```bash
# Run tests with debug output
mvn test -Dtest=LoginControllerTest -X

# Run single test with debug
mvn test -Dtest=LoginControllerTest#testMethod -Dtestfx.headless=false
```

## Continuous Integration

### GitHub Actions Example
```yaml
name: Frontend Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: '17'
      - name: Install Xvfb
        run: |
          sudo apt-get update
          sudo apt-get install -y xvfb
      - name: Run Tests
        run: |
          export DISPLAY=:99
          Xvfb :99 -screen 0 1024x768x24 &
          mvn test -Dtestfx.headless=true
```

## Coverage Reports

### Generating Coverage Reports
```bash
# Generate coverage report
mvn test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

### Coverage Targets
- Line coverage: > 80%
- Branch coverage: > 70%
- Method coverage: > 85%

## Contributing

### Adding New Tests
1. Create test class in appropriate package
2. Follow naming convention: `ClassNameTest`
3. Use `@ExtendWith(ApplicationExtension.class)`
4. Implement `@Start` method for initialization
5. Add comprehensive test methods
6. Include both positive and negative test cases

### Test Guidelines
- Write tests for all public methods
- Test edge cases and error conditions
- Use descriptive test names
- Keep tests focused and fast
- Mock external dependencies
- Clean up resources properly

### Code Review Checklist
- [ ] Tests cover all functionality
- [ ] Tests are independent and isolated
- [ ] Tests use appropriate assertions
- [ ] Tests handle cleanup properly
- [ ] Tests follow naming conventions
- [ ] Tests include error scenarios
- [ ] Tests are well-documented

## Resources

### Documentation
- [TestFX Documentation](https://testfx.github.io/TestFX/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)

### Examples
- See individual test classes for specific examples
- Check `FrontendTestSuite` for test organization
- Review `CalendarControllerTest` for complex UI testing

### Support
- Create issues for test failures
- Document new test requirements
- Update this README as needed
- Follow established patterns and conventions 