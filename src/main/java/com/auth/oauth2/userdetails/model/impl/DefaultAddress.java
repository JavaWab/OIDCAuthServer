package com.auth.oauth2.userdetails.model.impl;

import com.auth.oauth2.userdetails.model.Address;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * DefaultAddress
 *
 * @author Anbang Wang
 * @date 2016/12/16
 */
@Document(collection = "Address")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DefaultAddress implements Address {
    private static final long serialVersionUID = -1304880008685206811L;
    @Id
    private String id;
    private String formatted;
    private String streetAddress;
    private String locality;
    private String region;
    private String postalCode;
    private String country;

    public DefaultAddress() { }

    public DefaultAddress(Address address) {
        setFormatted(address.getFormatted());
        setStreetAddress(address.getStreetAddress());
        setLocality(address.getLocality());
        setRegion(address.getRegion());
        setPostalCode(address.getPostalCode());
        setCountry(address.getCountry());
    }

    @Override
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    @Override
    public String getFormatted() {
        return formatted;
    }

    @Override
    public void setFormatted(String formatted) {
        this.formatted = formatted;
    }

    @Override
    public String getStreetAddress() {
        return streetAddress;
    }

    @Override
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    @Override
    public String getLocality() {
        return locality;
    }

    @Override
    public void setLocality(String locality) {
        this.locality = locality;
    }

    @Override
    public String getRegion() {
        return region;
    }

    @Override
    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public String getPostalCode() {
        return postalCode;
    }

    @Override
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public String getCountry() {
        return country;
    }

    @Override
    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((country == null) ? 0 : country.hashCode());
        result = prime * result + ((formatted == null) ? 0 : formatted.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((locality == null) ? 0 : locality.hashCode());
        result = prime * result + ((postalCode == null) ? 0 : postalCode.hashCode());
        result = prime * result + ((region == null) ? 0 : region.hashCode());
        result = prime * result + ((streetAddress == null) ? 0 : streetAddress.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DefaultAddress)) {
            return false;
        }
        DefaultAddress other = (DefaultAddress) obj;
        if (country == null) {
            if (other.country != null) {
                return false;
            }
        } else if (!country.equals(other.country)) {
            return false;
        }
        if (formatted == null) {
            if (other.formatted != null) {
                return false;
            }
        } else if (!formatted.equals(other.formatted)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (locality == null) {
            if (other.locality != null) {
                return false;
            }
        } else if (!locality.equals(other.locality)) {
            return false;
        }
        if (postalCode == null) {
            if (other.postalCode != null) {
                return false;
            }
        } else if (!postalCode.equals(other.postalCode)) {
            return false;
        }
        if (region == null) {
            if (other.region != null) {
                return false;
            }
        } else if (!region.equals(other.region)) {
            return false;
        }
        if (streetAddress == null) {
            if (other.streetAddress != null) {
                return false;
            }
        } else if (!streetAddress.equals(other.streetAddress)) {
            return false;
        }
        return true;
    }
}
