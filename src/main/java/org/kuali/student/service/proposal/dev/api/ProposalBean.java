	/*
	 * Copyright 2010 The Kuali Foundation
	 *
	 * Licensed under the Educational Community License, Version 2.0 (the "License");
	 * you may not use this file except in compliance with the License.
	 * You may	obtain a copy of the License at
	 *
	 * 	http://www.osedu.org/licenses/ECL-2.0
	 *
	 * Unless required by applicable law or agreed to in writing, software
	 * distributed under the License is distributed on an "AS IS" BASIS,
	 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	 * See the License for the specific language governing permissions and
	 * limitations under the License.
	 */
	package org.kuali.student.service.proposal.dev.api;
	
	
	import java.io.Serializable;
	import java.util.Date;
	import java.util.List;
	import java.util.Map;
	
	
	public class ProposalBean
	 implements ProposalInfo	, Serializable
	{
		
		private static final long serialVersionUID = 1L;
		
		private String name;
		
		/**
		* Set Proposal Name
		*
		* Type: string
		*
		* The name or title of the proposal. Any finite sequence of characters with 
		* letters, numerals, symbols and punctuation marks. The length can be any natural 
		* number between zero or any positive integer.
		*/
		@Override
		public void setName(String name)
		{
			this.name = name;
		}
		
		/**
		* Get Proposal Name
		*
		* Type: string
		*
		* The name or title of the proposal. Any finite sequence of characters with 
		* letters, numerals, symbols and punctuation marks. The length can be any natural 
		* number between zero or any positive integer.
		*/
		@Override
		public String getName()
		{
			return this.name;
		}
						
		private List<String> proposerPerson;
		
		/**
		* Set Proposer Person
		*
		* Type: personIdList
		*
		* List of person identifiers. Structure should contain a proposerPerson OR a 
		* proposerOrg.
		*/
		@Override
		public void setProposerPerson(List<String> proposerPerson)
		{
			this.proposerPerson = proposerPerson;
		}
		
		/**
		* Get Proposer Person
		*
		* Type: personIdList
		*
		* List of person identifiers. Structure should contain a proposerPerson OR a 
		* proposerOrg.
		*/
		@Override
		public List<String> getProposerPerson()
		{
			return this.proposerPerson;
		}
						
		private List<String> proposerOrg;
		
		/**
		* Set Proposer Organization
		*
		* Type: orgIdList
		*
		* List of organization identifiers. Structure should contain a proposerPerson OR a 
		* proposerOrg
		*/
		@Override
		public void setProposerOrg(List<String> proposerOrg)
		{
			this.proposerOrg = proposerOrg;
		}
		
		/**
		* Get Proposer Organization
		*
		* Type: orgIdList
		*
		* List of organization identifiers. Structure should contain a proposerPerson OR a 
		* proposerOrg
		*/
		@Override
		public List<String> getProposerOrg()
		{
			return this.proposerOrg;
		}
						
		private String proposalReferenceType;
		
		/**
		* Set Proposal Reference Type
		*
		* Type: referenceTypeKey
		*
		* Unique identifier for a reference type.
		*/
		@Override
		public void setProposalReferenceType(String proposalReferenceType)
		{
			this.proposalReferenceType = proposalReferenceType;
		}
		
		/**
		* Get Proposal Reference Type
		*
		* Type: referenceTypeKey
		*
		* Unique identifier for a reference type.
		*/
		@Override
		public String getProposalReferenceType()
		{
			return this.proposalReferenceType;
		}
						
		private List<String> proposalReference;
		
		/**
		* Set Proposal Reference
		*
		* Type: referenceIdList
		*
		* List of reference identifiers.
		*/
		@Override
		public void setProposalReference(List<String> proposalReference)
		{
			this.proposalReference = proposalReference;
		}
		
		/**
		* Get Proposal Reference
		*
		* Type: referenceIdList
		*
		* List of reference identifiers.
		*/
		@Override
		public List<String> getProposalReference()
		{
			return this.proposalReference;
		}
						
		private String rationale;
		
		/**
		* Set Rationale
		*
		* Type: string
		*
		* Brief explanation of the reason for the proposal
		*/
		@Override
		public void setRationale(String rationale)
		{
			this.rationale = rationale;
		}
		
		/**
		* Get Rationale
		*
		* Type: string
		*
		* Brief explanation of the reason for the proposal
		*/
		@Override
		public String getRationale()
		{
			return this.rationale;
		}
						
		private String detailDesc;
		
		/**
		* Set Detailed Description
		*
		* Type: string
		*
		* Detailed description of the proposed changes.
		*/
		@Override
		public void setDetailDesc(String detailDesc)
		{
			this.detailDesc = detailDesc;
		}
		
		/**
		* Get Detailed Description
		*
		* Type: string
		*
		* Detailed description of the proposed changes.
		*/
		@Override
		public String getDetailDesc()
		{
			return this.detailDesc;
		}
						
		private Date effectiveDate;
		
		/**
		* Set Effective Date
		*
		* Type: dateTime
		*
		* Date and time that this proposal became effective. This is a similar concept to 
		* the effective date on enumerated values. When an expiration date has been 
		* specified, this field must be less than or equal to the expiration date.
		*/
		@Override
		public void setEffectiveDate(Date effectiveDate)
		{
			this.effectiveDate = effectiveDate;
		}
		
		/**
		* Get Effective Date
		*
		* Type: dateTime
		*
		* Date and time that this proposal became effective. This is a similar concept to 
		* the effective date on enumerated values. When an expiration date has been 
		* specified, this field must be less than or equal to the expiration date.
		*/
		@Override
		public Date getEffectiveDate()
		{
			return this.effectiveDate;
		}
						
		private Date expirationDate;
		
		/**
		* Set Expiration Date
		*
		* Type: dateTime
		*
		* Date and time that this proposal expires. This is a similar concept to the 
		* expiration date on enumerated values. If specified, this should be greater than 
		* or equal to the effective date. If this field is not specified, then no 
		* expiration date has been currently defined and should automatically be 
		* considered greater than the effective date.
		*/
		@Override
		public void setExpirationDate(Date expirationDate)
		{
			this.expirationDate = expirationDate;
		}
		
		/**
		* Get Expiration Date
		*
		* Type: dateTime
		*
		* Date and time that this proposal expires. This is a similar concept to the 
		* expiration date on enumerated values. If specified, this should be greater than 
		* or equal to the effective date. If this field is not specified, then no 
		* expiration date has been currently defined and should automatically be 
		* considered greater than the effective date.
		*/
		@Override
		public Date getExpirationDate()
		{
			return this.expirationDate;
		}
						
		private Map<String,String> attributes;
		
		/**
		* Set Generic/dynamic attributes
		*
		* Type: attributeInfoList
		*
		* List of key/value pairs, typically used for dynamic attributes.
		*/
		@Override
		public void setAttributes(Map<String,String> attributes)
		{
			this.attributes = attributes;
		}
		
		/**
		* Get Generic/dynamic attributes
		*
		* Type: attributeInfoList
		*
		* List of key/value pairs, typically used for dynamic attributes.
		*/
		@Override
		public Map<String,String> getAttributes()
		{
			return this.attributes;
		}
						
		private MetaInfo metaInfo;
		
		/**
		* Set Create/Update meta info
		*
		* Type: metaInfo
		*
		* Create and last update info for the structure. This is optional and treated as 
		* read only since the data is set by the internals of the service during 
		* maintenance operations.
		*/
		@Override
		public void setMetaInfo(MetaInfo metaInfo)
		{
			this.metaInfo = metaInfo;
		}
		
		/**
		* Get Create/Update meta info
		*
		* Type: metaInfo
		*
		* Create and last update info for the structure. This is optional and treated as 
		* read only since the data is set by the internals of the service during 
		* maintenance operations.
		*/
		@Override
		public MetaInfo getMetaInfo()
		{
			return this.metaInfo;
		}
						
		private String type;
		
		/**
		* Set Proposal Type
		*
		* Type: proposalTypeKey
		*
		* Unique identifier for a proposal type.
		*/
		@Override
		public void setType(String type)
		{
			this.type = type;
		}
		
		/**
		* Get Proposal Type
		*
		* Type: proposalTypeKey
		*
		* Unique identifier for a proposal type.
		*/
		@Override
		public String getType()
		{
			return this.type;
		}
						
		private String state;
		
		/**
		* Set Proposal State
		*
		* Type: string
		*
		* The current status of the proposal. The values for this field are constrained to 
		* those in the proposalState enumeration. A separate setup operation does not 
		* exist for retrieval of the meta data around this value.
		*/
		@Override
		public void setState(String state)
		{
			this.state = state;
		}
		
		/**
		* Get Proposal State
		*
		* Type: string
		*
		* The current status of the proposal. The values for this field are constrained to 
		* those in the proposalState enumeration. A separate setup operation does not 
		* exist for retrieval of the meta data around this value.
		*/
		@Override
		public String getState()
		{
			return this.state;
		}
						
		private String id;
		
		/**
		* Set Proposal Identifier
		*
		* Type: proposalId
		*
		* Unique identifier for a Proposal. This is optional, due to the identifier being 
		* set at the time of creation. Once the proposal has been created, this should be 
		* seen as required.
		*/
		@Override
		public void setId(String id)
		{
			this.id = id;
		}
		
		/**
		* Get Proposal Identifier
		*
		* Type: proposalId
		*
		* Unique identifier for a Proposal. This is optional, due to the identifier being 
		* set at the time of creation. Once the proposal has been created, this should be 
		* seen as required.
		*/
		@Override
		public String getId()
		{
			return this.id;
		}
						
	}

