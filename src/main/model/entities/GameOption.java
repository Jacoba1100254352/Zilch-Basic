package model.entities;


import rules.managers.RuleType;


/**
 * @param value Used for options like MULTIPLE or SINGLE
 */
public record GameOption(RuleType type, Integer value) {}
