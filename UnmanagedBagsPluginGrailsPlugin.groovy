import com.bluelobsterstudios.annotations.UnmanagedBags
import com.bluelobsterstudios.groovy.UnmanagedBagGenerator
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsClassUtils

import java.lang.annotation.Annotation

class UnmanagedBagsPluginGrailsPlugin {

    GrailsApplication grailsApplication

    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.2 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "Unmanaged Bags Plugin Plugin" // Headline display name of the plugin
    def author = "YoungSoul"
    def authorEmail = ""
    def description = '''\
Plugin to add dynamic method to support unmanaged hibernate bags
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/unmanaged-bags-plugin"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        grailsApplication = ctx.getBean('grailsApplication')
        UnmanagedBagGenerator.grailsApplication = grailsApplication


        grailsApplication.domainClasses.each { domainClass ->
            if( domainClass.clazz.isAnnotationPresent(UnmanagedBags)) {
                Annotation[] annotations = domainClass.clazz.getAnnotation(UnmanagedBags).all()
                if( annotations ) {
                    annotations.each { Annotation annotation ->
                        String parentKeyPropertyName = annotation.parentKeyPropertyName()
                        String collectionPropertyName = annotation.collectionPropertyName()
                        String parentFKPropertyName = annotation.parentFKPropertyName()
                        Class childClass = annotation.childClass()

                        UnmanagedBagGenerator.generateUnmanagedBagMethods(domainClass, parentKeyPropertyName, collectionPropertyName, parentFKPropertyName, childClass)

                    }
                }

            }

            //----------------------------  hasUnmanagedBag -----------------------------
            // check to see if the user used:
            // static hasUnmanagedBag = []
            if (GrailsClassUtils.isStaticProperty(domainClass.clazz, "hasUnmanagedBags")){

                Map bagSpec = domainClass.clazz.hasUnmanagedBags

                bagSpec?.each { bagKey,bagValue ->
                    String parentKeyPropertyName = 'id'
                    String parentFKPropertyName = 'ownerId'
                    String collectionPropertyName = null
                    Class childClass = null

                    if (bagValue instanceof Map ) {
                        Map bagMap = bagValue as Map
                        parentKeyPropertyName = bagMap.get('parentKeyPropertyName') ?: 'id'
                        parentFKPropertyName = bagMap.get('parentFKPropertyName') ?: 'ownerId'
                        collectionPropertyName = bagKey

                        // look for an entry that is not parentKeyPropertyName and not parentFKPropertyName
                        // we assume that key value is the collectionPropertyName
                        for( entry in bagMap ) {
                            if( entry.key == 'parentKeyPropertyName' ) {
                                parentKeyPropertyName = entry.value
                            }
                            if( entry.key == 'parentFKPropertyName' ) {
                                parentFKPropertyName = entry.value
                            }
                            if( entry.key == 'collectionPropertyName' ) {
                                collectionPropertyName = entry.value
                            }
                            if( entry.key == 'childClass') {
                                childClass = entry.value
                            }
                        }
                    } else {
                        // must just be the class
                        collectionPropertyName = bagKey
                        childClass = bagValue
                    }

                    UnmanagedBagGenerator.generateUnmanagedBagMethods(domainClass, parentKeyPropertyName, collectionPropertyName, parentFKPropertyName, childClass)
                }
            }
        }
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
