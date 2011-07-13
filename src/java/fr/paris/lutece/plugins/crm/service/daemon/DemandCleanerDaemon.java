/*
 * Copyright (c) 2002-2011, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.crm.service.daemon;

import fr.paris.lutece.plugins.crm.business.demand.Demand;
import fr.paris.lutece.plugins.crm.business.demand.DemandFilter;
import fr.paris.lutece.plugins.crm.business.demand.DemandType;
import fr.paris.lutece.plugins.crm.business.demand.DemandTypeFilter;
import fr.paris.lutece.plugins.crm.service.demand.DemandService;
import fr.paris.lutece.plugins.crm.service.demand.DemandTypeService;
import fr.paris.lutece.plugins.crm.util.OperatorEnum;
import fr.paris.lutece.plugins.crm.util.constants.CRMConstants;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 *
 * Daemon DemandCleanerDaemon
 *
 */
public class DemandCleanerDaemon extends Daemon
{
    /**
     * Daemon's treatment method
     */
    public void run(  )
    {
        int nExpirationDays = AppPropertiesService.getPropertyInt( CRMConstants.PROPERTY_DAEMON_NB_EXPIRATION_DAYS, 7 );
        Calendar calendar = new GregorianCalendar(  );
        calendar.add( Calendar.DATE, -nExpirationDays );

        DemandTypeFilter dtFilter = new DemandTypeFilter(  );
        dtFilter.setDateEnd( calendar.getTime(  ) );
        dtFilter.setOperatorDateEnd( OperatorEnum.LOWER_OR_EQUAL );

        for ( DemandType demandType : DemandTypeService.getService(  ).findByFilter( dtFilter ) )
        {
            DemandFilter dFilter = new DemandFilter(  );
            dFilter.setIdDemandType( demandType.getIdDemandType(  ) );

            for ( Demand demand : DemandService.getService(  ).findByFilter( dFilter ) )
            {
                if ( demand.getIdStatusCRM(  ) == 0 )
                {
                    // Only removing the demand that have the status draft
                    DemandService.getService(  ).remove( demand.getIdDemand(  ) );
                }
            }
        }
    }
}