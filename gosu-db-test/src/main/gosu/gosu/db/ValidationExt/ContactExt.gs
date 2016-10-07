package gosu.db.foo.ValidationExt

uses gosu.db.foo.Validation
uses gosu.db.foo.Validation.*

uses gosu.db.api.IModelConfig
uses gosu.db.runtime.GosuDBExt

class ContactExt extends GosuDBExt<Validation.Contact> {

  override function configure(cfg : IModelConfig) {

    cfg.addValidation(Contact#FirstName, \ val -> {
      if(val?.length() < 1) throw "First name must be non-empty"
    })

  }

}