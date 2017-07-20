package actor.proto.tests

import actor.proto.DeadLetterProcess
import actor.proto.PID
import actor.proto.ProcessNameExistException
import actor.proto.ProcessRegistry
import actor.proto.fixture.TestProcess
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import java.util.*
import kotlin.test.assertFailsWith

class ProcessRegistryTests {
    @Test fun `given pid does not exist, add should add local pid`() {
        val id = UUID.randomUUID().toString()
        val p = TestProcess()
        val reg = ProcessRegistry
        val pid = reg.add(id, p)
        assertEquals(reg.address, pid.address)
    }

    @Test fun `given pid exists, add should not add local pid`() {
        val id = UUID.randomUUID().toString()
        val p = TestProcess()
        val reg = ProcessRegistry
        reg.add(id, p)

        assertFailsWith<ProcessNameExistException> {
            reg.add(id, p)
        }
    }

    @Test fun `given pid exists, get should return it`() {
        val id = UUID.randomUUID().toString()
        val p = TestProcess()
        val reg = ProcessRegistry
        val pid = reg.add(id, p)
        val p2 = reg.get(pid)
        assertSame(p, p2)
    }

    @Test fun `given pid was removed, get should return deadLetter process`() {
        val id = UUID.randomUUID().toString()
        val p = TestProcess()
        val reg = ProcessRegistry
        val pid = reg.add(id, p)
        reg.remove(pid)
        val p2 = reg.get(pid)
        assertSame(DeadLetterProcess, p2)
    }

    @Test fun `given pid exists in host resolver, get should return it`() {
        val pid = PID("abc", "def")
        val p = TestProcess()
        val reg = ProcessRegistry
        reg.registerHostResolver { _ -> p }
        val p2 = reg.get(pid)
        assertSame(p, p2)
    }
}

