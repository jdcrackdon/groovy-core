package groovy.mock.interceptor

class HalfMockTest extends GroovyTestCase {

    void setUp() {
        Baz.constructorCalls = 0
        Baz.staticExistsCalls = 0
        Baz.existsCalls = 0
    }

    void testCallsConstructorOfMockedObject() {
        def mock = new MockFor(Baz)
        mock.use {
            def baz = new Baz()
        }
        assert Baz.constructorCalls == 1
    }

    void testMocksNonExistingMethods() {
        def mock = new MockFor(Baz)
        mock.demand.doesntExist() { 'testMocksNonExistingMethods' }
        mock.use {
            def baz = new Baz()
            assert baz.doesntExist() == 'testMocksNonExistingMethods'
        }
    }

    void testCallsExistingMethodsIfIgnored() {
        def mock = new MockFor(Baz)
        mock.ignore('exists')
        mock.use {
            def baz = new Baz()
            baz.exists()
        }
        assert Baz.existsCalls == 1
    }

    void testMocksExistingMethods() {
        def mock = new MockFor(Baz)
        mock.demand.exists() { 'testMocksExistingMethods' }
        mock.use {
            def baz = new Baz()
            assert baz.exists() == 'testMocksExistingMethods'
        }
        assert Baz.existsCalls == 0
    }

    void testMocksNonExistingStaticMethods() {
        def mock = new MockFor(Baz)
        mock.demand.staticDoesntExist() { 'testMocksNonExistingStaticMethods' }
        mock.use {
            def baz = new Baz()
            assert Baz.staticDoesntExist() == 'testMocksNonExistingStaticMethods'
        }
    }

    void testCallsExistingStaticMethodsIfIgnored() {
        def mock = new MockFor(Baz)
        mock.ignore('staticExists')
        mock.use {
            def baz = new Baz()
            Baz.staticExists()
        }
        assert Baz.staticExistsCalls == 1
    }

    void testMocksNonExistingProperties() {
        def mock = new MockFor(Baz)
        mock.demand.setNonExistingProperty() {}
        mock.demand.getNonExistingProperty() {2}
        mock.use {
            def baz = new Baz()
            baz.nonExistingProperty = 1
            assert baz.nonExistingProperty == 2
        }
    }

    void testAccessesExistingPropertiesIfIgnored() {
        def mock = new MockFor(Baz)
        mock.ignore(~'[sg]etExistingProperty')
        mock.use {
            Baz baz = new Baz()
            baz.existingProperty = 1
            assert baz.existingProperty == 1
        }
    }

    void testAccessesExistingInheritedPropertiesIfIgnored() {
        def mock = new MockFor(Bar)
        mock.ignore(~'[sg]etExistingProperty')
        mock.use {
            Baz bar = new Bar()
            bar.existingProperty = 1
            assert bar.existingProperty == 1
        }
    }

}

class Baz {

    static existsCalls = 0, staticExistsCalls = 0, constructorCalls = 0
    def existingProperty = 0

    Baz() {
        constructorCalls++
    }

    def exists() {
        existsCalls++
    }

    def callsDoesntExist() {
        doesntExist()
    }

    static void staticExists() {
        staticExistsCalls++
    }
}

class Bar extends Baz {

}