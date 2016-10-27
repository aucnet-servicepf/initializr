package ${packageName}.${artifactId};

import org.springframework.boot.builder.SpringApplicationBuilder;<% if (newServletInitializer) { %>
import org.springframework.boot.web.support.SpringBootServletInitializer;<% } else { %>
import org.springframework.boot.context.web.SpringBootServletInitializer;<% } %>

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(${applicationName}.class);
	}

}
