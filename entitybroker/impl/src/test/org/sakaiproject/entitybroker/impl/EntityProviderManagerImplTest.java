/**
 * $Id$
 * $URL$
 * EntityHandlerImplTest.java - entity-broker - Apr 6, 2008 12:08:39 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Centre for Applied Research in Educational Technologies, University of Cambridge
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 *
 * Aaron Zeckoski (azeckoski@gmail.com) (aaronz@vt.edu) (aaron@caret.cam.ac.uk)
 */

package org.sakaiproject.entitybroker.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.entitybroker.EntityView;
import org.sakaiproject.entitybroker.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.EntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.CollectionResolvable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Outputable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Resolvable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.TagSearchable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Taggable;
import org.sakaiproject.entitybroker.entityprovider.extension.ActionReturn;
import org.sakaiproject.entitybroker.entityprovider.extension.CustomAction;
import org.sakaiproject.entitybroker.impl.entityprovider.EntityProviderManagerImpl;
import org.sakaiproject.entitybroker.mocks.ActionsEntityProviderMock;
import org.sakaiproject.entitybroker.mocks.data.MyEntity;
import org.sakaiproject.entitybroker.mocks.data.TestData;

/**
 * Testing the entity provider manager
 * 
 * @author Aaron Zeckoski (aaronz@vt.edu)
 */
public class EntityProviderManagerImplTest extends TestCase {

   protected EntityProviderManagerImpl entityProviderManager;
   private TestData td;

   @Override
   protected void setUp() throws Exception {
      super.setUp();
      // setup things
      td = new TestData();
      entityProviderManager = new TestManager(td).entityProviderManager;
   }

   /**
    * Test method for
    * {@link org.sakaiproject.entitybroker.impl.entityprovider.EntityProviderManagerImpl#init()}.
    */
   public void testInit() {
      // simply make sure this does not fail
      entityProviderManager.init();
   }

   /**
    * Test method for
    * {@link org.sakaiproject.entitybroker.impl.entityprovider.EntityProviderManagerImpl#getProviderByReference(java.lang.String)}.
    */
   @SuppressWarnings("deprecation")
   public void testGetProviderByReference() {
      EntityProvider ep = null;

      // get valid providers
      ep = entityProviderManager.getProviderByReference(TestData.REF1);
      assertNotNull(ep);
      assertEquals(td.entityProvider1, ep);

      ep = entityProviderManager.getProviderByReference(TestData.REF3A);
      assertNotNull(ep);
      assertEquals(td.entityProvider3, ep);

      // test unregistered provider returns null
      ep = entityProviderManager.getProviderByReference(TestData.REF9);
      assertNull(ep);

      // ensure invalid prefix dies
      try {
         ep = entityProviderManager.getProviderByReference(TestData.INVALID_REF);
         fail("Should have thrown exception");
      } catch (IllegalArgumentException e) {
         assertNotNull(e.getMessage());
      }
   }

   /**
    * Test method for
    * {@link org.sakaiproject.entitybroker.impl.entityprovider.EntityProviderManagerImpl#getProviderByPrefix(java.lang.String)}.
    */
   public void testGetProviderByPrefix() {
      EntityProvider ep = null;

      // get valid providers
      ep = entityProviderManager.getProviderByPrefix(TestData.PREFIX1);
      assertNotNull(ep);
      assertEquals(td.entityProvider1, ep);

      ep = entityProviderManager.getProviderByPrefix(TestData.PREFIX2);
      assertNotNull(ep);
      assertEquals(td.entityProvider2, ep);

      ep = entityProviderManager.getProviderByPrefix(TestData.PREFIX3);
      assertNotNull(ep);
      assertEquals(td.entityProvider3, ep);

      // ensure invalid prefix simply returns null
      ep = entityProviderManager.getProviderByPrefix(TestData.INVALID_REF);
      assertNull(ep);
   }

   /**
    * Test method for
    * {@link org.sakaiproject.entitybroker.impl.entityprovider.EntityProviderManagerImpl#getProviderByPrefixAndCapability(java.lang.String, java.lang.Class)}.
    */
   public void testGetProviderByPrefixAndCapability() {
      EntityProvider ep = null;

      // get valid providers
      ep = entityProviderManager.getProviderByPrefixAndCapability(TestData.PREFIX1,
            CoreEntityProvider.class);
      assertNotNull(ep);
      assertEquals(td.entityProvider1, ep);

      // get valid providers for sub caps
      ep = entityProviderManager.getProviderByPrefixAndCapability(TestData.PREFIX1, Taggable.class);
      assertNotNull(ep);
      assertEquals(td.entityProvider1T, ep);

      ep = entityProviderManager.getProviderByPrefixAndCapability(TestData.PREFIX4,
            Resolvable.class);
      assertNotNull(ep);
      assertEquals(td.entityProvider4, ep);

      // attempt to get providers when there are none
      ep = entityProviderManager.getProviderByPrefixAndCapability(TestData.PREFIX2,
            CoreEntityProvider.class);
      assertNull(ep);

      ep = entityProviderManager.getProviderByPrefixAndCapability(TestData.PREFIX1,
            Resolvable.class);
      assertNull(ep);
   }

   /**
    * Test method for
    * {@link org.sakaiproject.entitybroker.impl.entityprovider.EntityProviderManagerImpl#getRegisteredPrefixes()}.
    */
   public void testGetRegisteredPrefixes() {
      Set<String> s = null;

      s = entityProviderManager.getRegisteredPrefixes();
      assertNotNull(s);
      assertFalse(s.isEmpty());
      assertTrue(s.contains(TestData.PREFIX1));
      assertTrue(s.contains(TestData.PREFIX2));
      assertTrue(s.contains(TestData.PREFIX3));
      assertTrue(s.contains(TestData.PREFIX4));
      assertFalse(s.contains(TestData.PREFIX9));
   }

   public void testGetPrefixCapabilities() {
      List<Class<? extends EntityProvider>> caps = null;

      caps = entityProviderManager.getPrefixCapabilities(TestData.PREFIX1);
      assertNotNull(caps);
      assertEquals(4, caps.size());
      assertTrue(caps.contains(EntityProvider.class));
      assertTrue(caps.contains(CoreEntityProvider.class));
      assertTrue(caps.contains(Taggable.class));
      assertTrue(caps.contains(TagSearchable.class));

      caps = entityProviderManager.getPrefixCapabilities(TestData.PREFIX4);
      assertNotNull(caps);
      assertEquals(5, caps.size());
      assertTrue(caps.contains(EntityProvider.class));
      assertTrue(caps.contains(CoreEntityProvider.class));
      assertTrue(caps.contains(Resolvable.class));
      assertTrue(caps.contains(CollectionResolvable.class));
      assertTrue(caps.contains(Outputable.class));

   }

   public void testGetRegisteredEntityCapabilities() {
      Map<String, List<Class<? extends EntityProvider>>> m = null;
      
      m = entityProviderManager.getRegisteredEntityCapabilities();
      assertNotNull(m);
      assertTrue(m.size() > 5);
      assertTrue(m.containsKey(TestData.PREFIX1));
      assertTrue(m.containsKey(TestData.PREFIX2));
      assertTrue(m.containsKey(TestData.PREFIX3));
      assertTrue(m.containsKey(TestData.PREFIX4));
      assertFalse(m.containsKey(TestData.PREFIX9));

      List<Class<? extends EntityProvider>> caps = m.get(TestData.PREFIX1);
      assertNotNull(caps);
      assertEquals(4, caps.size());
      assertTrue(caps.contains(EntityProvider.class));
      assertTrue(caps.contains(CoreEntityProvider.class));
      assertTrue(caps.contains(Taggable.class));
      assertTrue(caps.contains(TagSearchable.class));
      
   }

   /**
    * Test method for
    * {@link org.sakaiproject.entitybroker.impl.entityprovider.EntityProviderManagerImpl#registerEntityProvider(org.sakaiproject.entitybroker.entityprovider.EntityProvider)}.
    */
   public void testRegisterEntityProvider() {
      // test registering unregistered provider
      entityProviderManager.registerEntityProvider(td.entityProvider9);

      // test registering an already registered provider
      entityProviderManager.registerEntityProvider(td.entityProvider1);

      // test registering null dies horribly
      try {
         entityProviderManager.registerEntityProvider(null);
         fail("Should have thrown exception");
      } catch (NullPointerException e) {
         assertNotNull(e);
      }
   }

   /**
    * Test method for
    * {@link org.sakaiproject.entitybroker.impl.entityprovider.EntityProviderManagerImpl#unregisterEntityProvider(org.sakaiproject.entitybroker.entityprovider.EntityProvider)}.
    */
   public void testUnregisterEntityProvider() {
      // test unregistering registered EP
      entityProviderManager.unregisterEntityProvider(td.entityProvider1);

      // test unregistering non registered EP
      entityProviderManager.unregisterEntityProvider(td.entityProvider9);

      // test unregistering null dies horribly
      try {
         entityProviderManager.unregisterEntityProvider(null);
         fail("Should have thrown exception");
      } catch (NullPointerException e) {
         assertNotNull(e);
      }
   }

   /**
    * Test method for
    * {@link org.sakaiproject.entitybroker.impl.entityprovider.EntityProviderManagerImpl#unregisterEntityProviderByPrefix(java.lang.String)}.
    */
   public void testUnregisterEntityProviderByPrefix() {
      // test unregistering registered EP
      entityProviderManager.unregisterEntityProviderByPrefix(TestData.PREFIX1);
      assertNull(entityProviderManager.getProviderByPrefix(TestData.PREFIX1));

      entityProviderManager.unregisterEntityProviderByPrefix(TestData.PREFIX7);
      assertNull(entityProviderManager.getProviderByPrefix(TestData.PREFIX7));

      entityProviderManager.unregisterEntityProviderByPrefix(TestData.PREFIX8);
      assertNull(entityProviderManager.getProviderByPrefix(TestData.PREFIX8));

      entityProviderManager.unregisterEntityProviderByPrefix(TestData.PREFIXA);
      assertNull(entityProviderManager.getProviderByPrefix(TestData.PREFIXA));

      // test unregistering non registered EP
      entityProviderManager.unregisterEntityProviderByPrefix(TestData.PREFIX9);

      // test unregistering null dies horribly
      try {
         entityProviderManager.unregisterEntityProviderByPrefix(null);
         fail("Should have thrown exception");
      } catch (NullPointerException e) {
         assertNotNull(e);
      }
   }

   /**
    * Test method for
    * {@link org.sakaiproject.entitybroker.impl.entityprovider.EntityProviderManagerImpl#unregisterCapability(java.lang.String, java.lang.Class)}.
    */
   public void testUnregisterEntityProviderCapability() {
      // test unregistering an added capability
      entityProviderManager.unregisterCapability(TestData.PREFIX1, Taggable.class);
      assertNull(entityProviderManager.getProviderByPrefixAndCapability(TestData.PREFIX1,
            Taggable.class));

      // test unregistering something we just unregistered (should be ok)
      entityProviderManager.unregisterCapability(TestData.PREFIX1, Taggable.class);

      // test unregistering something not registered
      entityProviderManager.unregisterCapability(TestData.PREFIX2, Taggable.class);

      // test unregistering null dies horribly
      try {
         entityProviderManager.unregisterCapability(TestData.PREFIX3, null);
         fail("Should have thrown exception");
      } catch (NullPointerException e) {
         assertNotNull(e);
      }
   }

   /**
    * Test method for {@link org.sakaiproject.entitybroker.impl.entityprovider.EntityProviderManagerImpl#setCustomActions(java.lang.String, java.util.Map)}.
    */
   public void testSetCustomActions() {
      Map<String, CustomAction> actions = new HashMap<String, CustomAction>();
      actions.put("test", new CustomAction("test", EntityView.VIEW_SHOW));
      entityProviderManager.setCustomActions(TestData.PREFIXA1, actions);
      assertNotNull(entityProviderManager.getCustomAction(TestData.PREFIXA1, "test"));

      try {
         entityProviderManager.setCustomActions(TestData.PREFIX2, actions);
         fail("Should have thrown exception");
      } catch (IllegalArgumentException e) {
         assertNotNull(e.getMessage());
      }
   }

   /**
    * Test method for {@link org.sakaiproject.entitybroker.impl.entityprovider.EntityProviderManagerImpl#getCustomAction(java.lang.String, java.lang.String)}.
    */
   public void testGetCustomAction() {
      assertNotNull( entityProviderManager.getCustomAction(TestData.PREFIXA1, "xxx") );
      assertNotNull( entityProviderManager.getCustomAction(TestData.PREFIXA1, "double") );

      assertNull( entityProviderManager.getCustomAction(TestData.PREFIXA1, "apple") );
      assertNull( entityProviderManager.getCustomAction(TestData.PREFIX2, "action") );
      assertNull( entityProviderManager.getCustomAction(TestData.PREFIX5, "action") );
   }

   /**
    * Test method for {@link org.sakaiproject.entitybroker.impl.entityprovider.EntityProviderManagerImpl#removeCustomActions(java.lang.String)}.
    */
   public void testRemoveCustomActions() {
      assertNotNull( entityProviderManager.getCustomAction(TestData.PREFIXA1, "xxx") );
      entityProviderManager.removeCustomActions(TestData.PREFIXA1);
      assertNull( entityProviderManager.getCustomAction(TestData.PREFIXA1, "xxx") );      
   }

   public void testCustomActions() {
      ActionsEntityProviderMock aep = td.entityProviderA1;

      // check double operation works
      MyEntity me = (MyEntity) aep.getEntity( new EntityReference(TestData.REFA1) );
      int num = me.getNumber();
      ActionReturn ar = (ActionReturn) aep.doubleAction(new EntityView(new EntityReference(TestData.REFA1), null, null));
      MyEntity doubleMe = (MyEntity) ar.entityData;
      assertEquals(doubleMe.getNumber(), num * 2);
      assertEquals(me.getId(), doubleMe.getId());

      // make sure it works twice
      ar = (ActionReturn) aep.doubleAction(new EntityView(new EntityReference(TestData.REFA1), null, null));
      doubleMe = (MyEntity) ar.entityData;
      assertEquals(doubleMe.getNumber(), num * 2);

      // test xxx operation
      MyEntity me1 = (MyEntity) aep.getEntity( new EntityReference(TestData.REFA1) );
      assertFalse("xxx".equals(me1.extra));
      assertFalse("xxx".equals(me1.getStuff()));
      aep.xxxAction( new EntityReference(TestData.REFA1) );
      MyEntity xxxMe = (MyEntity) aep.getEntity( new EntityReference(TestData.REFA1) );
      assertEquals(me1.getId(), xxxMe.getId());
      assertTrue("xxx".equals(xxxMe.extra));
      assertTrue("xxx".equals(xxxMe.getStuff()));

      // test clear
      assertEquals(2, aep.myEntities.size());
      aep.executeActions(new EntityView(new EntityReference(TestData.PREFIXA1, ""), EntityView.VIEW_NEW, null), "clear", null);
      assertEquals(0, aep.myEntities.size());      
   }

}
