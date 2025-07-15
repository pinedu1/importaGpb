package importaGPB

import br.com.pinedu.importa.run.Run
import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import groovy.transform.CompileStatic
import grails.plugins.metadata.*

@CompileStatic
class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        //GrailsApp.run(Application, args)

        Run.main()

    }
}
